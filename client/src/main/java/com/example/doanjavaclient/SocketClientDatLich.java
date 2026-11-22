package com.example.doanjavaclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/** Client TCP gui yeu cau dat lich den Spring Boot Server. */
final class SocketClientDatLich {
    private SocketClientDatLich() {
    }

    static String ping(String host, int port) throws IOException {
        try (Socket socket = ketNoi(host, port);
             BufferedReader in = taoReader(socket);
             PrintWriter out = taoWriter(socket)) {
            out.println("PING");
            return in.readLine();
        }
    }

    static String datLich(String host, int port, long benhNhanId,
                          long bacSiId, long lichLamViecId, LocalDate ngayKham,
                          String khungGio, String trieuChung) throws IOException {
        try (Socket socket = ketNoi(host, port);
             BufferedReader in = taoReader(socket);
             PrintWriter out = taoWriter(socket)) {
            String request = SocketClientProtocol.taoLenhDatLich(
                    benhNhanId, bacSiId, lichLamViecId, ngayKham, khungGio, trieuChung);
            out.println(request);
            return in.readLine();
        }
    }

    static Socket ketNoi(String host, int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5_000);
        socket.setSoTimeout(60_000);
        return socket;
    }

    static BufferedReader taoReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
    }

    static PrintWriter taoWriter(Socket socket) throws IOException {
        return new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }
}
