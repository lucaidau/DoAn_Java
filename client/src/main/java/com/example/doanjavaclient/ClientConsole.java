package com.example.doanjavaclient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Man hinh dieu khien Client Console, doc thong tin kiem thu tu ban phim. */
public class ClientConsole {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        String host = args.length >= 1 ? args[0] : "127.0.0.1";
        int port = args.length >= 2 ? parsePort(args[1]) : 9999;

        System.out.println("============================================================");
        System.out.println(" DOAN JAVA - CLIENT CONSOLE DAT LICH KHAM");
        System.out.println(" Ket noi TCP Server: " + host + ":" + port);
        System.out.println("============================================================");

        try {
            System.out.println("[KET NOI] " + SocketClientProtocol.moTaPhanHoi(
                    SocketClientDatLich.ping(host, port)));
        } catch (IOException ex) {
            System.out.println("[CHUA KET NOI DUOC] " + ex.getMessage());
            System.out.println("Hay kiem tra Server Console va cong TCP " + port + ".");
            return;
        }

        while (true) {
            System.out.println();
            System.out.println("---------------- MENU CLIENT ----------------");
            System.out.println("1. Kiem thu nhieu Client dat cung mot khung gio (DA LUONG)");
            System.out.println("2. Gui mot yeu cau dat lich (1 Client)");
            System.out.println("3. Kiem tra ket noi Server (PING)");
            System.out.println("0. Thoat Client");
            System.out.print("Chon chuc nang: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1" -> kiemThuDaLuong(host, port);
                case "2" -> datLichMotClient(host, port);
                case "3" -> kiemTraServer(host, port);
                case "0" -> {
                    System.out.println("Da dong Client Console.");
                    return;
                }
                default -> System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private static void kiemThuDaLuong(String host, int port) {
        int soClient = docSoNguyen("So Client muon mo (1-50): ", 3, 1, 50);
        long benhNhanIdBatDau = docSoLong("ID benh nhan dau tien: ");
        long bacSiId = docSoLong("ID bac si: ");
        long scheduleId = docSoLong("ID lich lam viec (schedule ID): ");
        LocalDate ngayKham = docNgay("Ngay kham (yyyy-MM-dd): ");
        String khungGio = docChuoi("Khung gio vi du 08:00 - 08:20: ");
        String trieuChung = docChuoi("Trieu chung (dung chung cho cac Client): ");

        System.out.println();
        System.out.println("Xac nhan: " + soClient + " benh nhan se cung gui yeu cau vao "
                + "lich " + scheduleId + ", khung gio " + khungGio + ".");
        System.out.println("Luu y: yeu cau thanh cong se tao ban ghi lich_hen that trong SQL Server.");
        System.out.print("Nhan ENTER de bat dau, hoac nhap N de huy: ");
        if (SCANNER.nextLine().trim().equalsIgnoreCase("N")) {
            System.out.println("Da huy kiem thu.");
            return;
        }

        KiemThuNhieuClient.chay(host, port, soClient, benhNhanIdBatDau,
                bacSiId, scheduleId, ngayKham, khungGio, trieuChung);
    }

    private static void datLichMotClient(String host, int port) {
        long benhNhanId = docSoLong("ID benh nhan: ");
        long bacSiId = docSoLong("ID bac si: ");
        long scheduleId = docSoLong("ID lich lam viec: ");
        LocalDate ngayKham = docNgay("Ngay kham (yyyy-MM-dd): ");
        String khungGio = docChuoi("Khung gio: ");
        String trieuChung = docChuoi("Trieu chung: ");

        try {
            String response = SocketClientDatLich.datLich(host, port, benhNhanId,
                    bacSiId, scheduleId, ngayKham, khungGio, trieuChung);
            System.out.println(SocketClientProtocol.moTaPhanHoi(response));
        } catch (IOException ex) {
            System.out.println("Khong gui duoc yeu cau: " + ex.getMessage());
        }
    }

    private static void kiemTraServer(String host, int port) {
        try {
            System.out.println(SocketClientProtocol.moTaPhanHoi(
                    SocketClientDatLich.ping(host, port)));
        } catch (IOException ex) {
            System.out.println("Server khong phan hoi: " + ex.getMessage());
        }
    }

    private static int docSoNguyen(String prompt, int defaultValue, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = SCANNER.nextLine().trim();
            if (raw.isEmpty()) {
                return defaultValue;
            }
            try {
                int value = Integer.parseInt(raw);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Hien thi loi ben duoi.
            }
            System.out.printf("Vui long nhap so tu %d den %d.%n", min, max);
        }
    }

    private static long docSoLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                long value = Long.parseLong(SCANNER.nextLine().trim());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Hien thi loi ben duoi.
            }
            System.out.println("Vui long nhap mot ID so nguyen duong.");
        }
    }

    private static LocalDate docNgay(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(SCANNER.nextLine().trim());
            } catch (DateTimeParseException ex) {
                System.out.println("Ngay khong dung dinh dang. Vi du: 2026-11-22.");
            }
        }
    }

    private static String docChuoi(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Thong tin khong duoc de trong.");
        }
    }

    private static int parsePort(String value) {
        try {
            int port = Integer.parseInt(value);
            return port >= 1 && port <= 65_535 ? port : 9999;
        } catch (NumberFormatException ex) {
            return 9999;
        }
    }
}
