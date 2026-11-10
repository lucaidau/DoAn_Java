package com.example.doanjava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.doanjava.entity.ChuyenKhoa;
import com.example.doanjava.service.ChuyenKhoaService;

@RestController
@RequestMapping("/api/specialties")
@CrossOrigin
public class ChuyenKhoaController {

    private final ChuyenKhoaService chuyenKhoaService;

    public ChuyenKhoaController(ChuyenKhoaService chuyenKhoaService) {
        this.chuyenKhoaService = chuyenKhoaService;
    }

    // Lay danh sach, tim kiem va phan trang chuyen khoa
    @GetMapping
    public ResponseEntity<?> layToanBoChuyenKhoa(
            @RequestParam(name = "keyword", required = false) String tuKhoa,
            @RequestParam(name = "category", defaultValue = "all") String loaiChuyenKhoa,
            @RequestParam(name = "sort", defaultValue = "alpha") String sapXep,
            @RequestParam(name = "page", defaultValue = "0") int trang,
            @RequestParam(name = "size", defaultValue = "8") int kichThuoc) {

        return ResponseEntity.ok(chuyenKhoaService.timKiemChuyenKhoa(
                tuKhoa, loaiChuyenKhoa, sapXep, trang, kichThuoc));
    }

    // So bac si + bac si phu trach cua tung chuyen khoa
    @GetMapping("/summary")
    public ResponseEntity<?> thongKeTheoChuyenKhoa() {
        return ResponseEntity.ok(chuyenKhoaService.thongKeTheoChuyenKhoa());
    }

    // Lay chi tiet chuyen khoa
    @GetMapping("/{ma}")
    public ResponseEntity<?> layChuyenKhoaTheoMa(@PathVariable("ma") Long maChuyenKhoa) {
        ChuyenKhoa chuyenKhoa = chuyenKhoaService.layChuyenKhoaTheoMa(maChuyenKhoa);

        if (chuyenKhoa == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(chuyenKhoa);
    }
}
