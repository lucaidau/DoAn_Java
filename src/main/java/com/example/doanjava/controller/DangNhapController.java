package com.example.doanjava.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DangNhapController {
    @GetMapping("/")
    public String root()
    {
        return "auth/DangNhap";
    }
    @GetMapping( "/login")
    public String hienThiDangNhap() {
        return "auth/DangNhap";
    }

    @GetMapping("/access-denied")
    public String camTruyCap() {
        return "cam-truy-cap";
    }
}
