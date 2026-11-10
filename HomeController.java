package com.example.doanjava.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.doanjava.service.HomeService; 

@RestController
@RequestMapping("/api/home")
@CrossOrigin
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    // Lay du lieu trang chu
    @GetMapping
    public ResponseEntity<?> layDuLieuTrangChu() {
        return ResponseEntity.ok(homeService.layDuLieuTrangChu());
    }

    // Tim kiem bac si tu trang chu
    @GetMapping("/search")
    public ResponseEntity<?> timKiemTrangChu(
            @RequestParam(name = "keyword", required = false) String tuKhoa,
            @RequestParam(name = "specialtyId", required = false) Long maChuyenKhoa,
            @RequestParam(name = "date", required = false) LocalDate ngayKham) {

        Map<String, Object> ketQua = homeService.timKiemTrangChu(
                tuKhoa, maChuyenKhoa, ngayKham);

        return ResponseEntity.ok(ketQua);
    }
}
