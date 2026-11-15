package com.example.doanjava.controller;

import com.example.doanjava.service.ChuyenKhoaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient/specialties")
public class ChuyenKhoaController {
    private final ChuyenKhoaService chuyenKhoaService;

    public ChuyenKhoaController(ChuyenKhoaService chuyenKhoaService) {
        this.chuyenKhoaService = chuyenKhoaService;
    }

    @GetMapping
    public String hienThiDanhSachChuyenKhoa(Model model) {
        model.addAttribute("chuyenKhoas", chuyenKhoaService.layDanhSachChuyenKhoa());
        model.addAttribute("thongKeChuyenKhoa", chuyenKhoaService.layThongKeChuyenKhoa());
        model.addAttribute("soBacSiHocHam", chuyenKhoaService.demBacSiCoHocHamHocVi());
        return "BenhNhan/DanhSachChuyenKhoa";
    }
}
