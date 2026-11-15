package com.example.doanjava.controller;

import com.example.doanjava.service.HomeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping({"", "/dashboard"})
    public String hienThiTrangChu(Model model) {
        model.addAttribute("thongKe", homeService.layThongKe());
        model.addAttribute("chuyenKhoa", homeService.layChuyenKhoaNoiBat());
        model.addAttribute("thongKeChuyenKhoa", homeService.layThongKeChuyenKhoa());
        model.addAttribute("bacSiNoiBat", homeService.layBacSiNoiBat());
        return "BenhNhan/TrangChuBenhNhan";
    }
}
