package com.example.doanjava.server;

import com.example.doanjava.service.DatLichKhamService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TCP Socket server dung de tiep nhan cac Client kiem thu nghiep vu dat lich kham.
 * Server duoc khoi dong cung voi Spring Boot, nhung khong thay the Web Server.
 */
@Component
public class DatLichKhamSocketServer {
    private static final Logger log = LoggerFactory.getLogger(DatLichKhamSocketServer.class);

    @Value("${datlich.socket.enabled:true}")
    private boolean enabled;

    @Value("${datlich.socket.host:127.0.0.1}")
    private String host;

    @Value("${datlich.socket.port:9999}")
    private int port;

    @Value("${datlich.socket.workers:10}")
    private int workerCount;

    private final DatLichKhamService datLichKhamService;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private ExecutorService workerPool;
    private Thread acceptorThread;

    public DatLichKhamSocketServer(DatLichKhamService datLichKhamService) {
        this.datLichKhamService = datLichKhamService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void startServer() {
        if (!enabled || running) {
            return;
        }

        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(host), port), 50);
            port = serverSocket.getLocalPort();

            int soWorker = Math.max(1, workerCount);
            AtomicInteger sequence = new AtomicInteger(1);
            workerPool = Executors.newFixedThreadPool(soWorker, task -> {
                Thread thread = new Thread(task,
                        "dat-lich-worker-" + sequence.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            });

            running = true;
            log.info("============================================================");
            log.info("DatLichKhamSocketServer da san sang tai {}:{}", host, port);
            log.info("So luong worker xu ly dong thoi: {}", soWorker);
            log.info("Client kiem thu co the ket noi vao cong TCP {}", port);
            log.info("============================================================");

            acceptorThread = new Thread(this::chapNhanClient, "dat-lich-acceptor");
            acceptorThread.setDaemon(true);
            acceptorThread.start();
        } catch (IOException ex) {
            running = false;
            log.error("Khong the khoi dong TCP Socket Server tai {}:{} - {}",
                    host, port, ex.getMessage(), ex);
            closeResources();
        }
    }

    private void chapNhanClient() {
        while (running && serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(60_000);
                log.info("[ACCEPT] Client {} ket noi", clientSocket.getRemoteSocketAddress());
                workerPool.execute(new XuLyClientDatLichTask(clientSocket, datLichKhamService));
            } catch (SocketException ex) {
                if (running) {
                    log.warn("Socket accept bi loi: {}", ex.getMessage());
                }
                break;
            } catch (IOException ex) {
                if (running) {
                    log.warn("Khong the tiep nhan Client: {}", ex.getMessage());
                }
            }
        }
    }

    public int getPort() {
        return port;
    }

    @PreDestroy
    public synchronized void stopServer() {
        running = false;
        closeResources();
        log.info("DatLichKhamSocketServer da dung.");
    }

    private void closeResources() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                log.warn("Loi khi dong ServerSocket: {}", ex.getMessage());
            }
        }
        if (workerPool != null) {
            workerPool.shutdownNow();
        }
    }
}
