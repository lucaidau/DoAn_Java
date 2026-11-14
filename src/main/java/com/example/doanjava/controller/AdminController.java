package com.example.doanjava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @GetMapping("/admin/taiKhoan")
    public String QLTaiKhoan()
    {
        return "/admin/QLTaiKhoan";
    }

    @GetMapping("/admin/dashboard")
    public String Dashboard()
    {
        return "/admin/Dashboard";
    }

    @GetMapping("/admin/bacSi")
    public String QLBacSi()
    {
        return  "/admin/QLBacSi";
    }

    @GetMapping("/admin/chuyenKhoa")
    public String QLChuyenKhoa()
    {
        return "/admin/QLChuyenKhoa";
    }

}
