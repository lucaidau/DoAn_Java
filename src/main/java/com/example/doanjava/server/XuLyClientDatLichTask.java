package com.example.doanjava.server;

import com.example.doanjava.dto.AppointmentForm;
import com.example.doanjava.service.DatLichKhamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

/** Xu ly mot ket noi Client tren mot worker thread rieng. */
public class XuLyClientDatLichTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(XuLyClientDatLichTask.class);
    private static final int MAX_LINE_LENGTH = 8_192;

    private final Socket socket;
    private final DatLichKhamService datLichKhamService;

    public XuLyClientDatLichTask(Socket socket, DatLichKhamService datLichKhamService) {
        this.socket = socket;
        this.datLichKhamService = datLichKhamService;
    }

    @Override
    public void run() {
        String workerName = Thread.currentThread().getName();
        String clientAddress = String.valueOf(socket.getRemoteSocketAddress());
        log.info("[{}] Bat dau phuc vu Client {}", workerName, clientAddress);

        try (Socket clientSocket = socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                if (line.length() > MAX_LINE_LENGTH) {
                    out.println("ERR|" + encode("Yeu cau vuot qua kich thuoc cho phep.") + "|" + workerName);
                    break;
                }

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if ("QUIT".equalsIgnoreCase(line)) {
                    out.println("BYE");
                    break;
                }

                if ("PING".equalsIgnoreCase(line)) {
                    out.println("PONG|" + workerName);
                    continue;
                }

                if (line.startsWith("BOOK|")) {
                    xuLyDatLich(line, out, workerName, clientAddress);
                } else {
                    out.println("ERR|" + encode("Lenh khong hop le. Su dung BOOK, PING hoac QUIT.")
                            + "|" + workerName);
                }
            }
        } catch (IOException ex) {
            log.warn("[{}] Client {} ngat ket noi: {}", workerName, clientAddress, ex.getMessage());
        } finally {
            log.info("[{}] Ket thuc phuc vu Client {}", workerName, clientAddress);
        }
    }

    private void xuLyDatLich(String request, PrintWriter out,
                             String workerName, String clientAddress) {
        try {
            String[] fields = request.split("\\|", -1);
            if (fields.length != 7) {
                throw new IllegalArgumentException("Cau truc lenh BOOK khong hop le.");
            }

            Long benhNhanId = Long.parseLong(fields[1]);
            Long bacSiId = Long.parseLong(fields[2]);
            Long lichLamViecId = Long.parseLong(fields[3]);
            LocalDate ngayKham = LocalDate.parse(fields[4]);
            String khungGio = decode(fields[5]);
            String trieuChung = decode(fields[6]);

            AppointmentForm form = new AppointmentForm();
            form.setDoctorId(bacSiId);
            form.setScheduleId(lichLamViecId);
            form.setAppointmentDate(ngayKham);
            form.setTimeSlot(khungGio);
            form.setSymptoms(trieuChung);

            log.info("[{}] Nhan BOOK tu {} | benhNhanId={} | bacSiId={} | scheduleId={} | slot={}",
                    workerName, clientAddress, benhNhanId, bacSiId, lichLamViecId, khungGio);

            // Goi bean Spring duoc inject de @Transactional(SERIALIZABLE) cua service duoc ap dung.
            Long maLichHen = datLichKhamService.taoLichHen(benhNhanId, form);
            out.println("OK|" + maLichHen + "|" + workerName);
            log.info("[{}] DAT LICH THANH CONG | appointmentId={} | benhNhanId={}",
                    workerName, maLichHen, benhNhanId);
        } catch (IllegalArgumentException ex) {
            guiLoi(out, workerName, ex.getMessage());
            log.info("[{}] Tu choi yeu cau dat lich tu {}: {}",
                    workerName, clientAddress, ex.getMessage());
        } catch (Exception ex) {
            String message = "He thong gap loi khi xu ly dat lich. Vui long xem Server Console.";
            guiLoi(out, workerName, message);
            log.error("[{}] Loi xu ly BOOK tu {}: {}", workerName, clientAddress,
                    ex.getMessage(), ex);
        }
    }

    private void guiLoi(PrintWriter out, String workerName, String message) {
        String safeMessage = (message == null || message.isBlank())
                ? "Yeu cau khong the duoc xu ly." : message;
        out.println("ERR|" + encode(safeMessage) + "|" + workerName);
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
