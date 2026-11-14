package com.example.doanjava.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.nio.charset.StandardCharsets;
import com.example.doanjava.entity.*;
import com.example.doanjava.service.LichHenService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.*;
import java.util.*;
@Controller
public class LichHenController {
    private final LichHenService service;
    public LichHenController(LichHenService service) {
        this.service = service;
    }

    // -- Hiển thị danh sách và xử lý bộ lọc bằng tham số GET --
    @GetMapping("/lichhen")
    public String danhSach(@RequestParam(defaultValue = "") String search, @RequestParam(defaultValue = "ALL") String status, @RequestParam(defaultValue = "ALL") String period, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay, @RequestParam(defaultValue = "1") int page, Model model) {
        LocalDate homNay = LocalDate.now();
        switch (period.toUpperCase(Locale.ROOT)) {
            case "TODAY" -> { tuNgay = homNay; denNgay = homNay; }
            case "TOMORROW" -> { tuNgay = homNay.plusDays(1); denNgay = homNay.plusDays(1); }
            case "WEEK" -> { tuNgay = homNay.with(java.time.DayOfWeek.MONDAY); denNgay = tuNgay.plusDays(6); }
            case "CUSTOM" -> { }
            default -> period = "ALL";
        }
        search = search.trim();
        if (status.isBlank()) status = "ALL";
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            model.addAttribute("loi", "Ngày bắt đầu không được lớn hơn ngày kết thúc");
            LocalDate temp = tuNgay;
            tuNgay = denNgay;
            denNgay = temp;
        }
        Page<LichHen> ds = service.danhSach(search, status, tuNgay, denNgay, Math.max(1, page));
        if (ds.getTotalPages() > 0 && page > ds.getTotalPages()) {
            ds = service.danhSach(search, status, tuNgay, denNgay, ds.getTotalPages());
        }
        long tongTatCa = service.tongTatCa();
        long choDuyet = service.thongKe("CHO_XAC_NHAN");
        long daDuyet = service.thongKe("DA_XAC_NHAN");
        long dangKham = service.thongKe("DANG_KHAM");
        long daKham = service.thongKe("DA_KHAM");
        model.addAttribute("ds", ds);
        model.addAttribute("ngayHienTai", homNay);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("period", period);
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);
        model.addAttribute("tongTatCa", tongTatCa);
        model.addAttribute("tongHomNay", service.tongHomNay());
        model.addAttribute("choDuyet", choDuyet);
        model.addAttribute("daDuyet", daDuyet);
        model.addAttribute("dangKham", dangKham);
        model.addAttribute("daKham", daKham);
        model.addAttribute("tiLeHoanThanh", tongTatCa == 0 ? 0 : Math.round(daKham * 100.0 / tongTatCa));
        model.addAttribute("trangHienTai", ds.getNumber() + 1);
        model.addAttribute("tongTrang", ds.getTotalPages());
        model.addAttribute("dauTrang", Math.max(1, Math.min(ds.getNumber() + 1 - 1, Math.max(1, ds.getTotalPages() - 2))));
        return "DanhSachLichHen";
    }

    // -- Tải form và chỉ hiển thị ca khám của bác sĩ được chọn --
    @GetMapping("/lichhen/tao")
    public String formTao(@RequestParam(required = false) Long bacSiId, Model model) {
        Long bacSiDangNhap = service.bacSiIdHienTai();
        if (bacSiDangNhap != null) bacSiId = bacSiDangNhap;
        model.addAttribute("benhNhans", service.benhNhans());
        Long bacSiLoc = bacSiId;
        model.addAttribute("bacSis", bacSiDangNhap == null ? service.bacSis() : service.bacSis().stream().filter(b -> Objects.equals(b.getId(), bacSiLoc)).toList());
        model.addAttribute("bacSiId", bacSiId);
        model.addAttribute("tenBacSiDaChon", bacSiId == null ? "Chưa chọn bác sĩ" : service.bacSis().stream().filter(bs -> Objects.equals(bs.getId(), bacSiLoc)).map(bs -> bs.getHoTen()).findFirst().orElse("Không tìm thấy bác sĩ"));
        model.addAttribute("phongKhams", service.phongKhams());
        model.addAttribute("caKhams", bacSiId == null ? Collections.emptyList() : service.caKhams().stream().filter(ca -> ca.getBacSi() != null && Objects.equals(ca.getBacSi().getId(), bacSiLoc)).toList());
        return "taolich";
    }

    // -- Lưu lịch hẹn và trở về danh sách để cập nhật thống kê --
    @PostMapping("/lichhen/tao")
    public String tao(@RequestParam Long benhNhanId, @RequestParam Long bacSiId, @RequestParam Long caId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay, @RequestParam String gio, @RequestParam(defaultValue = "") String trieuChung, @RequestParam Long phongId, @RequestParam(defaultValue = "") String ghiChu, @RequestParam(required = false) Integer tamThu, @RequestParam(required = false) Integer tamTruong, @RequestParam(required = false) Integer nhipTim, @RequestParam(required = false) Integer spo2, @RequestParam(required = false) java.math.BigDecimal canNang, @RequestParam(required = false) java.math.BigDecimal chieuCao, @RequestParam(required = false) String tienSu, @RequestParam(required = false) String giaDinh, @RequestParam(required = false) String thoiQuen, RedirectAttributes flash) {
        try {
            service.taoDayDu(benhNhanId, bacSiId, caId, ngay, gio, trieuChung, phongId, ghiChu, tamThu, tamTruong, nhipTim, spo2, canNang, chieuCao, tienSu, giaDinh, thoiQuen);
            flash.addFlashAttribute("thanhCong", "Đã tạo lịch hẹn thành công");
            return "redirect:/lichhen";
        } catch (Exception e) {
            flash.addFlashAttribute("loi", e.getMessage());
            return "redirect:/lichhen/tao?bacSiId=" + bacSiId;
        }
    }

    @GetMapping("/lichhen/{id}")
    public String chiTiet(@PathVariable Long id, Model model) {
        LichHen lich = service.chiTiet(id);
        model.addAttribute("lich", lich);
        model.addAttribute("hoSo", service.hoSo(id).orElse(null));
        model.addAttribute("tuoi", lich.getBenhNhan().getNgaySinh() == null ? null : Math.max(0, Period.between(lich.getBenhNhan().getNgaySinh(), LocalDate.now()).getYears()));
        model.addAttribute("lichSuKhams", service.lichSuKham(id));
        model.addAttribute("sinhHieu", service.sinhHieu(id).orElse(null));
        model.addAttribute("thanhToan", service.thanhToan(id).orElse(null));
        return "ChiTietLichHen";
    }

    @PostMapping("/lichhen/{id}/{hanhDong:duyet|tuchoi|batdaukham}")
    public String trangThai(@PathVariable Long id, @PathVariable String hanhDong, RedirectAttributes flash) {
        service.chiTiet(id);
        try {
            service.doiTrangThai(id, hanhDong);
            flash.addFlashAttribute("thanhCong", "Cập nhật trạng thái thành công");
        } catch (Exception e) {
            flash.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/lichhen/" + id;
    }

    @PostMapping("/lichhen/{id}/doigio")
    public String doiGio(@PathVariable Long id, @RequestParam String gio, RedirectAttributes flash) {
        service.chiTiet(id);
        try {
            service.doiGio(id, gio);
            flash.addFlashAttribute("thanhCong", "Đã cập nhật khung giờ");
        } catch (Exception e) {
            flash.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/lichhen/" + id;
    }

    @GetMapping("/lichhen/{id}/khambenh")
    public String khamBenh(@PathVariable Long id, Model model) {
        LichHen lich = service.chiTiet(id);
        if (!List.of("DANG_KHAM", "DA_KHAM").contains(lich.getTrangThai())) return "redirect:/lichhen/" + id;
        HoSoBenhAn hoSo = service.hoSo(id).orElse(null);
        model.addAttribute("lich", lich);
        model.addAttribute("hoSo", hoSo);
        model.addAttribute("thuocs", hoSo == null ? List.of() : service.donThuoc(hoSo.getId()));
        model.addAttribute("danhMucThuoc", service.thuocs());
        model.addAttribute("sinhHieu", service.sinhHieu(id).orElse(null));
        model.addAttribute("tuoi", lich.getBenhNhan().getNgaySinh() == null ? null : Period.between(lich.getBenhNhan().getNgaySinh(), LocalDate.now()).getYears());
        return "CaKhamBenh";
    }

    @PostMapping("/lichhen/{id}/luukham")
    public String luuKham(@PathVariable Long id, @RequestParam String chanDoan, @RequestParam(defaultValue = "") String loiDan, @RequestParam(required = false) String[] tenThuoc, @RequestParam(required = false) String[] lieuLuong, @RequestParam(required = false) Integer[] soLuong, @RequestParam(required = false) String[] huongDan, @RequestParam(defaultValue = "false") boolean hoanThanh, @RequestParam(defaultValue = "") String timMach, @RequestParam(defaultValue = "") String hoHap, @RequestParam(defaultValue = "") String tieuHoa, @RequestParam(defaultValue = "") String chanDoanPhu, @RequestParam(defaultValue = "") String canLamSang, @RequestParam(defaultValue = "") String huongDieuTri, @RequestParam(defaultValue = "") String tienSu, @RequestParam(defaultValue = "") String giaDinh, @RequestParam(defaultValue = "") String thoiQuen, RedirectAttributes flash) {
        service.chiTiet(id);
        try {
            service.luuKhamDayDu(id, chanDoan, loiDan, tenThuoc, lieuLuong, soLuong, huongDan, hoanThanh, timMach, hoHap, tieuHoa, chanDoanPhu, canLamSang, huongDieuTri, tienSu, giaDinh, thoiQuen);
            flash.addFlashAttribute("thanhCong", hoanThanh ? "Đã hoàn thành ca khám" : "Đã lưu hồ sơ bệnh án");
        } catch (Exception e) {
            flash.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/lichhen/" + id + "/khambenh";
    }

    @GetMapping("/thongbao")
    public String thongBao(Model model) {
        model.addAttribute("lichHens", service.thongBao());
        return "thongbao";
    }

    @GetMapping("/catruc")
    public String caTruc(Model model) {
        Long bacSiId = service.bacSiIdHienTai();
        model.addAttribute("caKhams", bacSiId == null ? service.caKhams() : service.caKhamsTheoBacSi(bacSiId));
        return "catruc";
    }


    //Xuất danh sách lịch hẹn theo bộ lọc hiện tại
    @GetMapping("/lichhen/xuat")
    public ResponseEntity<byte[]> xuatDanhSach(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        LocalDate homNay = LocalDate.now();
        switch (period.toUpperCase(Locale.ROOT)) {
            case "TODAY" -> {
                tuNgay = homNay;
                denNgay = homNay;
            }
            case "TOMORROW" -> {
                tuNgay = homNay.plusDays(1);
                denNgay = tuNgay;
            }
            case "WEEK" -> {
                tuNgay = homNay.with(DayOfWeek.MONDAY);
                denNgay = tuNgay.plusDays(6);
            }
            case "CUSTOM", "ALL" -> {
            }
            default -> {
                tuNgay = null;
                denNgay = null;
            }
        }
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            return ResponseEntity.badRequest().body(
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc".getBytes(StandardCharsets.UTF_8));
        }
        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("Mã hẹn,Tên bệnh nhân,Ngày khám,Khung giờ,Triệu chứng,Trạng thái\r\n");
        int trang = 1;
        Page<LichHen> ds;
        do {
            ds = service.danhSach(search, status, tuNgay, denNgay, trang);
            for (LichHen lich : ds.getContent()) {
                csv.append(csvCell("LH-" + lich.getId())).append(",");
                csv.append(csvCell(lich.getBenhNhan().getHoTen())).append(",");
                csv.append(csvCell(lich.getNgayKham())).append(",");
                csv.append(csvCell(lich.getKhungGioKham())).append(",");
                csv.append(csvCell(lich.getTrieuChungBenh())).append(",");
                csv.append(csvCell(lich.getTrangThai())).append("\r\n");
            }
            trang++;
        } while (ds.hasNext());
        byte[] noiDung = csv.toString().getBytes(StandardCharsets.UTF_8);
        String tenFile = "DanhSachLichHen_" + homNay + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + tenFile + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(noiDung);
    }

    //Chuẩn hóa dữ liệu để xuất CSV an toàn
    private String csvCell(Object value) {
        String text = value == null ? "" : value.toString();
        if (!text.isEmpty() && "=+-@\t\r".indexOf(text.charAt(0)) >= 0) {
            text = "'" + text;
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard() {
        return "redirect:/lichhen";
    }


}
