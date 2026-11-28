package com.example.doanjava.controller;

import com.example.doanjava.dto.CuocTroChuyenDTO;
import com.example.doanjava.dto.NguoiLienHeDTO;
import com.example.doanjava.dto.TinNhanDTO;
import com.example.doanjava.entity.CuocTroChuyen;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.entity.TinNhan;
import com.example.doanjava.repository.TaiKhoanRepository;
import com.example.doanjava.service.NguoiLienHeService;
import com.example.doanjava.service.TinNhanService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat gồm 2 phần:
 *  1) WebSocket: gửi tin (/app/chat.gui) và nhận tin mới ngay lập tức (/user/queue/tinNhan).
 *  2) HTTP (/chat/api/...): tải dữ liệu ban đầu như danh sách, lịch sử, số tin chưa đọc, đánh dấu đã đọc.
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final int SO_TIN_MOI_LAN = 50;
    private static final String KENH_TIN_NHAN = "/queue/tinNhan";

    private final TinNhanService tinNhanService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final NguoiLienHeService nguoiLienHeService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(TinNhanService tinNhanService,
                          TaiKhoanRepository taiKhoanRepository,
                          NguoiLienHeService nguoiLienHeService,
                          SimpMessagingTemplate messagingTemplate) {
        this.tinNhanService = tinNhanService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.nguoiLienHeService = nguoiLienHeService;
        this.messagingTemplate = messagingTemplate;
    }

    public record GuiTinNhanReq(Long idTroChuyen, String noiDung) { }   // gửi qua WebSocket
    public record GuiTinHttpReq(String noiDung) { }                     // gửi qua HTTP (id cuộc trò chuyện nằm trên đường dẫn)
    public record BatDauReq(Long doiPhuongId) { }

    // =====================================================================
    // PHẦN 1: WEBSOCKET
    // =====================================================================

    /**
     * Trình duyệt gửi tin tới /app/chat.gui.
     * Server lưu tin rồi đẩy bản sao tới CẢ HAI người (người gửi và người nhận).
     */
    @MessageMapping("/chat.gui")
    public void guiTinNhan(GuiTinNhanReq yeuCau, Principal principal) {
        Long idNguoiGui = layIdTuWebSocket(principal);
        luuVaDayTin(idNguoiGui, yeuCau.idTroChuyen(), yeuCau.noiDung());
    }

    /**
     * Lưu một tin nhắn rồi đẩy tới cả hai người qua WebSocket. Cả hai cách gửi (WebSocket và HTTP) đều dùng hàm này.
     * Trả về bản tin dành cho người gửi.
     */
    private TinNhanDTO luuVaDayTin(Long idNguoiGui, Long idTroChuyen, String noiDung) {
        // Lưu tin (đã kiểm tra: nội dung, người gửi có thuộc cuộc trò chuyện, người nhận không bị khóa)
        TinNhan tinNhan = tinNhanService.guiTinNhan(idNguoiGui, idTroChuyen, noiDung);

        CuocTroChuyen cuoc = tinNhan.getCuocTroChuyen();
        TaiKhoan nguoiGui = cuoc.layNguoiDung(idNguoiGui);
        TaiKhoan nguoiNhan = cuoc.layDoiPhuong(idNguoiGui);
        String tenNguoiGui = tinNhanService.layTenHienThi(nguoiGui.getId());

        // Mỗi người nhận một bản DTO riêng, vì "laCuaToi" phụ thuộc vào người đang xem
        TinNhanDTO banChoNguoiGui = TinNhanDTO.tuEntity(tinNhan, nguoiGui.getId(), tenNguoiGui);
        guiChoNguoiDung(nguoiGui.getId(), banChoNguoiGui);
        guiChoNguoiDung(nguoiNhan.getId(), TinNhanDTO.tuEntity(tinNhan, nguoiNhan.getId(), tenNguoiGui));
        return banChoNguoiGui;
    }

    /** Có lỗi khi gửi (ví dụ tin trống, người nhận bị khóa): báo lại riêng cho người gửi ở /user/queue/loi. */
    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/queue/loi")
    public Map<String, String> loiKhiGui(IllegalArgumentException loi) {
        return Map.of("loi", loi.getMessage() == null ? "Không gửi được tin nhắn" : loi.getMessage());
    }

    private void guiChoNguoiDung(Long idTaiKhoan, TinNhanDTO tin) {
        // Tên người dùng ở đây là id tài khoản dạng chữ (do ChatHandshakeHandler đặt khi kết nối)
        messagingTemplate.convertAndSendToUser(String.valueOf(idTaiKhoan), KENH_TIN_NHAN, tin);
    }

    private Long layIdTuWebSocket(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Bạn chưa đăng nhập!");
        }
        try {
            return Long.valueOf(principal.getName());
        } catch (NumberFormatException loi) {
            throw new IllegalArgumentException("Kết nối chat không hợp lệ, hãy tải lại trang!");
        }
    }

    // =====================================================================
    // PHẦN 2: HTTP (tải dữ liệu)
    // =====================================================================

    /** Số tin chưa đọc, để hiện huy hiệu đỏ trên nút chat. */
    @GetMapping("/api/chuaDoc")
    public Map<String, Long> chuaDoc(Principal principal) {
        TaiKhoan nguoiDung = layTaiKhoanHienTai(principal);
        return Map.of("tong", tinNhanService.demTinChuaDoc(nguoiDung.getId()));
    }

    /** Danh sách cuộc trò chuyện của tôi. */
    @GetMapping("/api/cuocTroChuyen")
    public List<CuocTroChuyenDTO> danhSachCuoc(Principal principal) {
        return tinNhanService.layDanhSach(layTaiKhoanHienTai(principal).getId());
    }

    /** Những người tôi được phép bắt đầu nhắn tin (tìm theo tên bằng tham số q). */
    @GetMapping("/api/lienHe")
    public List<NguoiLienHeDTO> lienHe(@RequestParam(required = false) String q, Principal principal) {
        return nguoiLienHeService.layDanhSach(layTaiKhoanHienTai(principal).getId(), q);
    }

    /** Bắt đầu (hoặc mở lại) cuộc trò chuyện với một người. */
    @PostMapping("/api/cuocTroChuyen/batDau")
    public Map<String, Long> batDau(@RequestBody BatDauReq yeuCau, Principal principal) {
        TaiKhoan nguoiDung = layTaiKhoanHienTai(principal);
        CuocTroChuyen cuoc = tinNhanService.layHoacTao(nguoiDung.getId(), yeuCau.doiPhuongId());
        return Map.of("cuocId", cuoc.getId());
    }

    /** Lịch sử tin nhắn của một cuộc trò chuyện (từ cũ đến mới). */
    @GetMapping("/api/cuocTroChuyen/{idTroChuyen}/tinNhan")
    public List<TinNhanDTO> lichSu(@PathVariable Long idTroChuyen,
                                   @RequestParam(defaultValue = "0") int page,
                                   Principal principal) {
        Long idNguoiDung = layTaiKhoanHienTai(principal).getId();
        List<TinNhan> danhSachTin = tinNhanService.layLichSu(idNguoiDung, idTroChuyen, page, SO_TIN_MOI_LAN);

        // Cuộc trò chuyện chỉ có 2 người nên chỉ tra tên tối đa 2 lần
        Map<Long, String> tenTheoId = new HashMap<>();
        return danhSachTin.stream().map(tin -> {
            Long idNguoiGui = tin.getNguoiGui().getId();
            String tenNguoiGui = tenTheoId.computeIfAbsent(idNguoiGui, tinNhanService::layTenHienThi);
            return TinNhanDTO.tuEntity(tin, idNguoiDung, tenNguoiGui);
        }).toList();
    }

    /**
     * Gửi tin bằng HTTP POST. Dùng khi WebSocket chưa kết nối được (và để tương thích với giao diện cũ).
     * Tin vẫn được đẩy realtime tới người nhận nếu họ đang online.
     */
    @PostMapping("/api/cuocTroChuyen/{idTroChuyen}/tinNhan")
    public TinNhanDTO guiBangHttp(@PathVariable Long idTroChuyen,
                                  @RequestBody GuiTinHttpReq yeuCau,
                                  Principal principal) {
        Long idNguoiGui = layTaiKhoanHienTai(principal).getId();
        return luuVaDayTin(idNguoiGui, idTroChuyen, yeuCau.noiDung());
    }

    /** Đánh dấu các tin của đối phương trong cuộc trò chuyện này là đã đọc. */
    @PostMapping("/api/cuocTroChuyen/{idTroChuyen}/daDoc")
    public Map<String, Integer> daDoc(@PathVariable Long idTroChuyen, Principal principal) {
        int soTinCapNhat = tinNhanService.danhDauDaDoc(layTaiKhoanHienTai(principal).getId(), idTroChuyen);
        return Map.of("soTinCapNhat", soTinCapNhat);
    }

    /** Lỗi nghiệp vụ của các API HTTP trả về mã 400 kèm câu thông báo. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> loiNghiepVu(IllegalArgumentException loi) {
        String thongBao = (loi.getMessage() == null) ? "Yêu cầu không hợp lệ" : loi.getMessage();
        return ResponseEntity.badRequest().body(Map.of("loi", thongBao));
    }

    private TaiKhoan layTaiKhoanHienTai(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Bạn chưa đăng nhập!");
        }
        return taiKhoanRepository.findByTenDangNhapIgnoreCase(principal.getName())
                .or(() -> taiKhoanRepository.findByEmailIgnoreCase(principal.getName()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đăng nhập!"));
    }
}