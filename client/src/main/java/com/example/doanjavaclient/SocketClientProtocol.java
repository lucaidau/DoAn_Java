package com.example.doanjavaclient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

/** Quy uoc trao doi du lieu giua Client Console va TCP Socket Server. */
final class SocketClientProtocol {
    private SocketClientProtocol() {
    }

    static String taoLenhDatLich(long benhNhanId, long bacSiId, long lichLamViecId,
                                  LocalDate ngayKham, String khungGio, String trieuChung) {
        return "BOOK|" + benhNhanId
                + "|" + bacSiId
                + "|" + lichLamViecId
                + "|" + ngayKham
                + "|" + encode(khungGio)
                + "|" + encode(trieuChung);
    }

    static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    static String moTaPhanHoi(String response) {
        if (response == null || response.isBlank()) {
            return "Server khong gui phan hoi.";
        }
        String[] parts = response.split("\\|", -1);
        if (parts[0].equals("OK") && parts.length >= 3) {
            return "DAT LICH THANH CONG | Ma lich hen: " + parts[1]
                    + " | Server worker: " + parts[2];
        }
        if (parts[0].equals("ERR") && parts.length >= 3) {
            String message;
            try {
                message = decode(parts[1]);
            } catch (IllegalArgumentException ex) {
                message = "Phan hoi loi khong dung dinh dang.";
            }
            return "DAT LICH THAT BAI | " + message + " | Server worker: " + parts[2];
        }
        if (parts[0].equals("PONG")) {
            return "Server dang hoat dong | Worker: "
                    + (parts.length >= 2 ? parts[1] : "unknown");
        }
        return response;
    }
}
