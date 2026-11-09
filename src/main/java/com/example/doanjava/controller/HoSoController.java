package com.example.doanjava.controller;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.BenhNhanRepository;
import com.example.doanjava.repository.TaiKhoanRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import vn.carehealth.dto.CapNhatHoSoBenhNhan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HoSoController {
    private final TaiKhoanRepository taiKhoanRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final BacSiRepository bacSiRepository;

    public HoSoController(
            TaiKhoanRepository taiKhoanRepository,
            BenhNhanRepository benhNhanRepository,
            BacSiRepository bacSiRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.bacSiRepository = bacSiRepository;
    }

    private TaiKhoan layTaiKhoan(Authentication authentication) {
        return taiKhoanRepository.findByTenDangNhapIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Tài khoản không tồn tại"));
    }

    @GetMapping("/patient/profile")
    public String hoSoBenhNhan(Authentication authentication, Model model) {
        TaiKhoan taiKhoan = layTaiKhoan(authentication);
        BenhNhan benhNhan = benhNhanRepository.findFirstByTaiKhoanId(taiKhoan.getId())
                .orElseThrow(() -> new IllegalStateException("Chưa có hồ sơ bệnh nhân"));
        model.addAttribute("taiKhoan", taiKhoan);
        model.addAttribute("benhNhan", benhNhan);
        if (benhNhan.getNgaySinh() != null) {
            int tuoi = java.time.Period.between(
                    benhNhan.getNgaySinh(),
                    java.time.LocalDate.now()
            ).getYears();
            model.addAttribute("tuoi", tuoi);
        }
        return "BenhNhan/HoSoCaNhan";
    }

    @PostMapping("/patient/profile/update")
    @Transactional
    public String capNhatHoSoBenhNhan(
            Authentication authentication,
            @Valid @ModelAttribute CapNhatHoSoBenhNhan duLieu,
            BindingResult ketQua,
            RedirectAttributes thongBao) {
        TaiKhoan taiKhoan = layTaiKhoan(authentication);
        BenhNhan benhNhan = benhNhanRepository.findFirstByTaiKhoanId(taiKhoan.getId())
                .orElseThrow(() -> new IllegalStateException("Chưa có hồ sơ bệnh nhân"));

        if (ketQua.hasErrors()) {
            thongBao.addFlashAttribute("loiCapNhat", ketQua.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/patient/profile";
        }

        String email = duLieu.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        String soDienThoai = duLieu.getSoDienThoai().trim();

        if (taiKhoanRepository.findByEmailIgnoreCase(email)
                .filter(tk -> !tk.getId().equals(taiKhoan.getId())).isPresent()) {
            thongBao.addFlashAttribute("loiCapNhat", "Email đã được tài khoản khác sử dụng.");
            return "redirect:/patient/profile";
        }
        if (taiKhoanRepository.findBySoDienThoai(soDienThoai).stream()
                .anyMatch(tk -> !tk.getId().equals(taiKhoan.getId()))) {
            thongBao.addFlashAttribute("loiCapNhat", "Số điện thoại đã được tài khoản khác sử dụng.");
            return "redirect:/patient/profile";
        }

        taiKhoan.setEmail(email);
        taiKhoan.setSoDienThoai(soDienThoai);
        benhNhan.setHoTen(duLieu.getHoTen().trim());
        benhNhan.setNgaySinh(duLieu.getNgaySinh());
        benhNhan.setGioiTinh(duLieu.getGioiTinh());
        benhNhan.setNhomMau(duLieu.getNhomMau() == null || duLieu.getNhomMau().isBlank() ? null : duLieu.getNhomMau());
        benhNhan.setDiaChi(duLieu.getDiaChi() == null ? "" : duLieu.getDiaChi().trim());
        benhNhan.setTienSuBenh(duLieu.getTienSuBenh() == null ? "" : duLieu.getTienSuBenh().trim());

        taiKhoanRepository.save(taiKhoan);
        benhNhanRepository.save(benhNhan);
        thongBao.addFlashAttribute("capNhatThanhCong", "Cập nhật hồ sơ thành công!");
        return "redirect:/patient/profile";
    }

    @GetMapping("/doctor/profile")
    public String hoSoBacSi(Authentication authentication, Model model) {
        TaiKhoan taiKhoan = layTaiKhoan(authentication);
        model.addAttribute("taiKhoan", taiKhoan);
        if ("ADMIN".equals(taiKhoan.getVaiTro())) {
            model.addAttribute("tenBacSi", "Quản trị viên");
            return "BacSi/HoSoBacSi";
        }
        BacSi bacSi = bacSiRepository.findByTaiKhoanId(taiKhoan.getId())
                .orElseThrow(() -> new IllegalStateException("Chưa có hồ sơ bác sĩ"));
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("tenBacSi", bacSi.getHoTen());
        return "BacSi/HoSoBacSi";
    }
    @PostMapping("/doctor/profile/update")
@Transactional
public String capNhatHoSoBacSi(
        Authentication authentication,
        @RequestParam("hoTen") String hoTen,
        @RequestParam("hocHamHocVi") String hocHamHocVi,
        @RequestParam("soNamKinhNghiem") Integer soNamKinhNghiem,
        @RequestParam("gioiThieu") String gioiThieu,
        RedirectAttributes thongBao
) {

    TaiKhoan taiKhoan = layTaiKhoan(authentication);

    BacSi bacSi = bacSiRepository
            .findByTaiKhoanId(taiKhoan.getId())
            .orElseThrow(
                    () -> new IllegalStateException(
                            "Không tìm thấy hồ sơ bác sĩ"
                    )
            );

    if (hoTen == null || hoTen.trim().isEmpty()) {

        thongBao.addFlashAttribute(
                "loiCapNhat",
                "Họ tên bác sĩ không được để trống!"
        );

        return "redirect:/doctor/profile";
    }

    if (soNamKinhNghiem == null
            || soNamKinhNghiem < 0
            || soNamKinhNghiem > 80) {

        thongBao.addFlashAttribute(
                "loiCapNhat",
                "Số năm kinh nghiệm không hợp lệ!"
        );

        return "redirect:/doctor/profile";
    }

    bacSi.setHoTen(hoTen.trim());

    bacSi.setHocHamHocVi(
            hocHamHocVi == null
                    ? ""
                    : hocHamHocVi.trim()
    );

    bacSi.setSoNamKinhNghiem(soNamKinhNghiem);

    bacSi.setGioiThieu(
            gioiThieu == null
                    ? ""
                    : gioiThieu.trim()
    );

    bacSiRepository.saveAndFlush(bacSi);

    thongBao.addFlashAttribute(
            "capNhatThanhCong",
            "Cập nhật hồ sơ bác sĩ thành công!"
    );

    return "redirect:/doctor/profile";
}
}
