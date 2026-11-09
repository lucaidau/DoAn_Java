package com.example.doanjava.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DangNhapController {
    @GetMapping({"/", "/login"})
    public String hienThiDangNhap() {
        return "DangNhap";
    }

    @GetMapping("/access-denied")
    public String camTruyCap() {
        return "cam-truy-cap";
    }
}
