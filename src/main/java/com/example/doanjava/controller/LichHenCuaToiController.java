package com.example.doanjava.controller;

import com.example.doanjava.service.LichHenCuaToiService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Controller
@RequestMapping("/patient/appointments")
public class LichHenCuaToiController {
    private final LichHenCuaToiService lichHenCuaToiService;

    public LichHenCuaToiController(LichHenCuaToiService lichHenCuaToiService) {
        this.lichHenCuaToiService = lichHenCuaToiService;
    }

    @GetMapping
    public String hienThiLichHen(
            Authentication authentication,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(defaultValue = "all-time") String period,
            Model model) {
        String tuKhoa = search == null ? "" : search.trim();
        String trangThai = status == null || status.isBlank() ? "ALL" : status.toUpperCase(Locale.ROOT);
        String kyHan = period == null || period.isBlank() ? "all-time" : period.toLowerCase(Locale.ROOT);
        LichHenCuaToiService.DuLieuTrang duLieu =
                lichHenCuaToiService.layDuLieu(authentication, tuKhoa, trangThai, specialtyId, kyHan);
        model.addAttribute("benhNhan", duLieu.benhNhan());
        model.addAttribute("lichHens", duLieu.lichHens());
        model.addAttribute("chuyenKhoas", duLieu.chuyenKhoas());
        model.addAttribute("tongTatCa", duLieu.tongTatCa());
        model.addAttribute("sapToi", duLieu.sapToi());
        model.addAttribute("choXacNhan", duLieu.choXacNhan());
        model.addAttribute("daKham", duLieu.daKham());
        model.addAttribute("daHuy", duLieu.daHuy());
        model.addAttribute("search", tuKhoa);
        model.addAttribute("status", trangThai);
        model.addAttribute("specialtyId", specialtyId);
        model.addAttribute("period", kyHan);
        return "BenhNhan/LichHenCuaToi";
    }
}
