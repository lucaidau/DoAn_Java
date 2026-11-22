package com.example.doanjavaclient;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Mo nhieu ket noi Socket rieng va cho cac Client gui BOOK cung mot thoi diem. */
final class KiemThuNhieuClient {
    private KiemThuNhieuClient() {
    }

    static void chay(String host, int port, int soClient, long benhNhanIdBatDau,
                     long bacSiId, long lichLamViecId, LocalDate ngayKham,
                     String khungGio, String trieuChung) {
        if (soClient < 1 || soClient > 50) {
            System.out.println("So Client phai nam trong khoang 1 den 50.");
            return;
        }

        System.out.println();
        System.out.println("============================================================");
        System.out.println("BAT DAU KIEM THU DAT LICH DONG THOI QUA TCP SOCKET");
        System.out.printf("So Client: %d | Server: %s:%d%n", soClient, host, port);
        System.out.printf("Bac si: %d | Lich lam viec: %d | Ngay: %s | Khung gio: %s%n",
                bacSiId, lichLamViecId, ngayKham, khungGio);
        System.out.println("Moi Client dung mot benh nhan khac nhau nhung cung dat mot khung gio.");
        System.out.println("============================================================");

        ExecutorService pool = Executors.newFixedThreadPool(soClient, new java.util.concurrent.ThreadFactory() {
            private int sequence = 1;

            @Override
            public synchronized Thread newThread(Runnable task) {
                return new Thread(task, "client-dat-lich-" + sequence++);
            }
        });

        CountDownLatch sanSang = new CountDownLatch(soClient);
        CountDownLatch tinHieuBatDau = new CountDownLatch(1);
        CountDownLatch hoanTat = new CountDownLatch(soClient);
        List<String> phanHoi = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < soClient; i++) {
            final int clientNumber = i + 1;
            final long benhNhanId = benhNhanIdBatDau + i;
            pool.submit(() -> {
                String tenClient = "Client-" + clientNumber;
                boolean daBaoSanSang = false;
                try (Socket socket = SocketClientDatLich.ketNoi(host, port);
                     BufferedReader in = SocketClientDatLich.taoReader(socket);
                     PrintWriter out = SocketClientDatLich.taoWriter(socket)) {

                    sanSang.countDown();
                    daBaoSanSang = true;
                    if (!tinHieuBatDau.await(20, TimeUnit.SECONDS)) {
                        throw new IOExceptionClient("Het thoi gian cho tin hieu bat dau.");
                    }

                    String request = SocketClientProtocol.taoLenhDatLich(
                            benhNhanId, bacSiId, lichLamViecId,
                            ngayKham, khungGio, trieuChung);
                    out.println(request);
                    String response = in.readLine();
                    if (response == null) {
                        throw new IOExceptionClient("Server dong ket noi truoc khi tra ket qua.");
                    }

                    phanHoi.add(response);
                    synchronized (System.out) {
                        System.out.printf("[%s | %s | BenhNhan ID=%d] %s%n",
                                tenClient, Thread.currentThread().getName(), benhNhanId,
                                SocketClientProtocol.moTaPhanHoi(response));
                    }
                } catch (Exception ex) {
                    if (!daBaoSanSang) {
                        sanSang.countDown();
                    }
                    String loi = "CLIENT_ERROR|" + ex.getMessage();
                    phanHoi.add(loi);
                    synchronized (System.out) {
                        System.out.printf("[%s | %s | BenhNhan ID=%d] LOI: %s%n",
                                tenClient, Thread.currentThread().getName(), benhNhanId,
                                ex.getMessage());
                    }
                } finally {
                    hoanTat.countDown();
                }
            });
        }

        try {
            boolean duClientSanSang = sanSang.await(15, TimeUnit.SECONDS);
            if (!duClientSanSang) {
                System.out.println("Canh bao: khong phai tat ca Client ket noi duoc trong 15 giay.");
            }
            System.out.println("[CLIENT CONSOLE] Tat ca ket noi san sang - phat tin hieu BOOK cung luc!");
            tinHieuBatDau.countDown();

            if (!hoanTat.await(70, TimeUnit.SECONDS)) {
                System.out.println("Canh bao: mot so Client chua hoan tat trong thoi gian cho.");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Kiem thu bi ngat.");
        } finally {
            tinHieuBatDau.countDown();
            pool.shutdownNow();
        }

        long thanhCong = phanHoi.stream().filter(s -> s.startsWith("OK|")).count();
        long thatBai = phanHoi.stream().filter(s -> s.startsWith("ERR|")).count();
        long loiKetNoi = phanHoi.stream().filter(s -> s.startsWith("CLIENT_ERROR|")).count();

        System.out.println("------------------------------------------------------------");
        System.out.printf("Tong Client co phan hoi: %d/%d%n", phanHoi.size(), soClient);
        System.out.printf("Dat lich thanh cong: %d%n", thanhCong);
        System.out.printf("Bi tu choi do nghiep vu: %d%n", thatBai);
        System.out.printf("Loi ket noi/Client: %d%n", loiKetNoi);
        System.out.println("KET THUC KIEM THU DA LUONG.");
        System.out.println("============================================================");
    }

    private static final class IOExceptionClient extends Exception {
        private static final long serialVersionUID = 1L;

        private IOExceptionClient(String message) {
            super(message);
        }
    }
}
