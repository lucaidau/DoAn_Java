package com.example.doanjava.config;

import com.example.doanjava.service.NguoiLienHeService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * Chạy một lần khi trình duyệt mở kết nối WebSocket.
 * Nhiệm vụ: lấy người đang đăng nhập (từ phiên đăng nhập sẵn có) rồi đặt TÊN KẾT NỐI = id tài khoản.
 * Nhờ vậy server gửi thông báo cho "người dùng có id 5" là Spring tự đưa tới đúng kết nối của người đó,
 * người khác không thể nhận được.
 */
@Component
public class ChatHandshakeHandler extends DefaultHandshakeHandler {

    private final NguoiLienHeService nguoiLienHeService;

    public ChatHandshakeHandler(NguoiLienHeService nguoiLienHeService) {
        this.nguoiLienHeService = nguoiLienHeService;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest yeuCau,
                                      WebSocketHandler boXuLy,
                                      Map<String, Object> thuocTinh) {
        Principal nguoiDangNhap = yeuCau.getPrincipal();
        if (nguoiDangNhap == null) {
            return null;   // chưa đăng nhập
        }

        try {
            Long idTaiKhoan = nguoiLienHeService.layIdTheoDangNhap(nguoiDangNhap.getName());
            return new NguoiDungChat(String.valueOf(idTaiKhoan));
        } catch (IllegalArgumentException khongTimThayTaiKhoan) {
            return null;
        }
    }

    /** Danh tính của một kết nối: tên chính là id tài khoản dạng chữ, ví dụ "5". */
    private static class NguoiDungChat implements Principal {
        private final String ten;

        NguoiDungChat(String ten) {
            this.ten = ten;
        }

        @Override
        public String getName() {
            return ten;
        }
    }
}