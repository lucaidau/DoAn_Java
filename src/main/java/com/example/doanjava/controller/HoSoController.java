
package com.example.doanjava.controller;

import com.example.doanjava.dto.CapNhatHoSoBenhNhan;
import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.service.HoSoService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Period;

@Controller
public class HoSoController {

    private final HoSoService hoSoService;

    public HoSoController(HoSoService hoSoService) {
        this.hoSoService = hoSoService;
    }

    // HIỂN THỊ HỒ SƠ BỆNH NHÂN

    @GetMapping("/patient/profile")
    public String hoSoBenhNhan(
            Authentication authentication,
            Model model
    ) {

        TaiKhoan taiKhoan = hoSoService.layTaiKhoan(
                authentication
        );

        BenhNhan benhNhan = hoSoService.layBenhNhan(
                taiKhoan
        );

        model.addAttribute("taiKhoan", taiKhoan);

        model.addAttribute("benhNhan", benhNhan);

        if (benhNhan.getNgaySinh() != null) {

            int tuoi = Period.between(
                    benhNhan.getNgaySinh(),
                    LocalDate.now()
            ).getYears();

            model.addAttribute("tuoi", tuoi);
        }

        return "BenhNhan/HoSoCaNhan";
    }

    // CẬP NHẬT HỒ SƠ BỆNH NHÂN

    @PostMapping("/patient/profile/update")
    public String capNhatHoSoBenhNhan(
            Authentication authentication,
            @Valid @ModelAttribute CapNhatHoSoBenhNhan duLieu,
            BindingResult ketQua,
            RedirectAttributes thongBao
    ) {

        if (ketQua.hasErrors()) {

            thongBao.addFlashAttribute(
                    "loiCapNhat",
                    ketQua.getAllErrors()
                            .get(0)
                            .getDefaultMessage()
            );

            return "redirect:/patient/profile";
        }

        try {

            hoSoService.capNhatHoSoBenhNhan(
                    authentication,
                    duLieu
            );

            thongBao.addFlashAttribute(
                    "capNhatThanhCong",
                    "Cập nhật hồ sơ thành công!"
            );

        } catch (IllegalArgumentException e) {

            thongBao.addFlashAttribute(
                    "loiCapNhat",
                    e.getMessage()
            );
        }

        return "redirect:/patient/profile";
    }

    // HIỂN THỊ HỒ SƠ BÁC SĨ

    @GetMapping("/doctor/profile")
    public String hoSoBacSi(
            Authentication authentication,
            Model model
    ) {

        TaiKhoan taiKhoan = hoSoService.layTaiKhoan(
                authentication
        );

        model.addAttribute("taiKhoan", taiKhoan);

        if ("ADMIN".equals(taiKhoan.getVaiTro())) {

            model.addAttribute(
                    "tenBacSi",
                    "Quản trị viên"
            );

            return "BacSi/HoSoBacSi";
        }

        BacSi bacSi = hoSoService.layBacSi(
                taiKhoan
        );

        model.addAttribute("bacSi", bacSi);

        model.addAttribute(
                "tenBacSi",
                bacSi.getHoTen()
        );

        return "BacSi/HoSoBacSi";
    }

    // CẬP NHẬT HỒ SƠ BÁC SĨ

    @PostMapping("/doctor/profile/update")
    public String capNhatHoSoBacSi(
            Authentication authentication,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("hocHamHocVi") String hocHamHocVi,
            @RequestParam("soNamKinhNghiem") Integer soNamKinhNghiem,
            @RequestParam("gioiThieu") String gioiThieu,
            RedirectAttributes thongBao
    ) {

        try {

            hoSoService.capNhatHoSoBacSi(
                    authentication,
                    hoTen,
                    hocHamHocVi,
                    soNamKinhNghiem,
                    gioiThieu
            );

            thongBao.addFlashAttribute(
                    "capNhatThanhCong",
                    "Cập nhật hồ sơ bác sĩ thành công!"
            );

        } catch (IllegalArgumentException e) {

            thongBao.addFlashAttribute(
                    "loiCapNhat",
                    e.getMessage()
            );
        }

        return "redirect:/doctor/profile";
    }
}
