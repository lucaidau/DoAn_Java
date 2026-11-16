package com.example.doanjava.controller;

import com.example.doanjava.service.BacSiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient/doctors")
public class BacSiController {
    private final BacSiService bacSiService;

    public BacSiController(BacSiService bacSiService) {
        this.bacSiService = bacSiService;
    }

    @GetMapping
    public String hienThiDanhSachBacSi(Model model) {
        model.addAttribute("bacSis", bacSiService.layDanhSachBacSi());
        model.addAttribute("lichSapToi", bacSiService.layLichSapToi());
        model.addAttribute("chuyenKhoas", bacSiService.layDanhSachChuyenKhoa());
        model.addAttribute("thongKeChuyenKhoa", bacSiService.layThongKeChuyenKhoa());
        return "BenhNhan/DanhSachBacSi";
    }
}
