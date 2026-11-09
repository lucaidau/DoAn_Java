package com.example.doanjava.controller;

import com.example.doanjava.dto.ThongTinDatLich;
import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.entity.ChuyenKhoa;
import com.example.doanjava.entity.LichHen;
import com.example.doanjava.entity.LichLamViec;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.BenhNhanRepository;
import com.example.doanjava.repository.ChuyenKhoaRepository;
import com.example.doanjava.repository.TaiKhoanRepository;
import com.example.doanjava.service.DatLichKhamService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PatientController {
    private final DatLichKhamService datLichKhamService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final BacSiRepository bacSiRepository;
    private final ChuyenKhoaRepository chuyenKhoaRepository;

    public PatientController(DatLichKhamService datLichKhamService,
                             TaiKhoanRepository taiKhoanRepository,
                             BenhNhanRepository benhNhanRepository,
                             BacSiRepository bacSiRepository,
                             ChuyenKhoaRepository chuyenKhoaRepository) {
        this.datLichKhamService = datLichKhamService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.bacSiRepository = bacSiRepository;
        this.chuyenKhoaRepository = chuyenKhoaRepository;
    }

    private BenhNhan layBenhNhanDangDangNhap(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("Vui lòng đăng nhập bằng tài khoản bệnh nhân.");
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản đang đăng nhập."));
        return benhNhanRepository.findFirstByTaiKhoanId(taiKhoan.getId())
                .orElseThrow(() -> new IllegalStateException("Tài khoản chưa có hồ sơ bệnh nhân."));
    }

    @GetMapping("/patient/dashboard")
    public String trangChuBenhNhan(Authentication authentication, Model model) {
        model.addAttribute("benhNhan", layBenhNhanDangDangNhap(authentication));
        return "BenhNhan/TrangChuBenhNhan";
    }

    @GetMapping("/patient/doctors")
    public String danhSachBacSi(@RequestParam(required = false) String keyword, Model model) {
        List<BacSi> danhSach = bacSiRepository.findAllByOrderByHoTenAsc();
        if (keyword != null && !keyword.isBlank()) {
            String tuKhoa = keyword.trim().toLowerCase(java.util.Locale.ROOT);
            danhSach = danhSach.stream()
                    .filter(bacSi -> bacSi.getHoTen().toLowerCase(java.util.Locale.ROOT).contains(tuKhoa)
                            || (bacSi.getChuyenKhoa() != null
                            && bacSi.getChuyenKhoa().getTenChuyenKhoa().toLowerCase(java.util.Locale.ROOT).contains(tuKhoa)))
                    .toList();
        }
        model.addAttribute("danhSachBacSi", danhSach);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "BenhNhan/DanhSachBacsi";
    }

    @GetMapping("/bac-si/tim-kiem")
    public String timKiemBacSi(@RequestParam(required = false) String keyword) {
        return "redirect:/patient/doctors?keyword=" + (keyword == null ? "" : java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8));
    }

    @GetMapping("/patient/booking")
    public String moTrangDatLich() {
        return "redirect:/patient/doctors";
    }

    @GetMapping("/patient/lich-hen")
    public String danhSachLichHen(Authentication authentication, Model model) {
        BenhNhan benhNhan = layBenhNhanDangDangNhap(authentication);
        model.addAttribute("danhSachLichHen", datLichKhamService.layDanhSachLichHenCuaBenhNhan(benhNhan.getId()));
        model.addAttribute("benhNhan", benhNhan);
        return "BenhNhan/DanhSachLichHenBenhNhan";
    }

    @GetMapping("/patient/specialties")
    public String danhSachChuyenKhoa(Model model) {
        List<ChuyenKhoa> danhSach = chuyenKhoaRepository.findAllByOrderByTenChuyenKhoaAsc();
        model.addAttribute("danhSachChuyenKhoa", danhSach);
        return "BenhNhan/DanhSachChuyenKhoa";
    }

    @GetMapping("/ho-so-bac-si/{id}")
    public String chiTietBacSi(@PathVariable Long id, Model model) {
        BacSi bacSi = datLichKhamService.layBacSi(id);
        if (bacSi == null) {
            return "redirect:/patient/doctors";
        }
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("danhSachLichLamViec", datLichKhamService.layDanhSachCa(id));
        return "BenhNhan/ChiTietBacSi";
    }

    @GetMapping("/dat-lich-kham")
    public String hienThiDatLich(@RequestParam Long bacSiId,
                                 @RequestParam Long lichLamViecId,
                                 Authentication authentication,
                                 Model model) {
        BenhNhan benhNhan = layBenhNhanDangDangNhap(authentication);
        BacSi bacSi = datLichKhamService.layBacSi(bacSiId);
        LichLamViec ca = datLichKhamService.layCaLamViec(lichLamViecId);
        if (bacSi == null || ca == null || ca.getBacSi() == null
                || !ca.getBacSi().getId().equals(bacSiId)) {
            return "redirect:/patient/doctors";
        }
        ThongTinDatLich thongTin = new ThongTinDatLich();
        thongTin.setBacSiId(bacSiId);
        thongTin.setLichLamViecId(lichLamViecId);
        thongTin.setNgayKham(ca.getNgayLamViec());
        napDuLieuTrangDatLich(model, bacSi, ca, benhNhan, thongTin);
        return "BenhNhan/DatLichKham";
    }

    @PostMapping("/dat-lich-kham")
    public String taoLichHen(@Valid @ModelAttribute("thongTinDatLich") ThongTinDatLich thongTin,
                             BindingResult ketQua,
                             Authentication authentication,
                             Model model) {
        BenhNhan benhNhan = layBenhNhanDangDangNhap(authentication);
        if (thongTin.getBacSiId() == null || thongTin.getLichLamViecId() == null) {
            return "redirect:/patient/doctors";
        }
        BacSi bacSi = datLichKhamService.layBacSi(thongTin.getBacSiId());
        LichLamViec ca = datLichKhamService.layCaLamViec(thongTin.getLichLamViecId());

        if (bacSi == null || ca == null || ca.getBacSi() == null
                || !ca.getBacSi().getId().equals(thongTin.getBacSiId())) {
            return "redirect:/patient/doctors";
        }

        if (ketQua.hasErrors()) {
            model.addAttribute("error", ketQua.getAllErrors().get(0).getDefaultMessage());
            napDuLieuTrangDatLich(model, bacSi, ca, benhNhan, thongTin);
            return "BenhNhan/DatLichKham";
        }

        try {
            Long maLichHen = datLichKhamService.taoLichHen(benhNhan.getId(), thongTin);
            return "redirect:/dat-lich-kham/thanh-cong/" + maLichHen;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            napDuLieuTrangDatLich(model, bacSi, ca, benhNhan, thongTin);
            return "BenhNhan/DatLichKham";
        }
    }

    @GetMapping("/dat-lich-kham/thanh-cong/{id}")
    public String datLichThanhCong(@PathVariable Long id,
                                   Authentication authentication,
                                   Model model) {
        BenhNhan benhNhan = layBenhNhanDangDangNhap(authentication);
        LichHen lichHen = datLichKhamService.layLichHenCuaBenhNhan(id, benhNhan.getId());
        if (lichHen == null) {
            return "redirect:/patient/dashboard";
        }
        model.addAttribute("lichHen", lichHen);
        return "BenhNhan/DatLichKhamThanhCong";
    }

    private void napDuLieuTrangDatLich(Model model, BacSi bacSi, LichLamViec ca,
                                       BenhNhan benhNhan, ThongTinDatLich thongTin) {
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("lichLamViec", ca);
        model.addAttribute("benhNhan", benhNhan);
        model.addAttribute("thongTinDatLich", thongTin);
        model.addAttribute("khungGioConTrong", datLichKhamService.layKhungGioConTrong(ca.getId()));
    }
}
