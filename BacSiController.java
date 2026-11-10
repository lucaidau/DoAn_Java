package com.example.doanjava.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.service.BacSiService;


@RestController
@RequestMapping("/api/doctors")
@CrossOrigin
public class BacSiController {

    private final BacSiService bacSiService;

    public BacSiController(BacSiService bacSiService) {
        this.bacSiService = bacSiService;
    }

    // Lay danh sach, tim kiem va loc bac si
    @GetMapping
    public ResponseEntity<?> layToanBoBacSi(
            @RequestParam(name = "keyword", required = false) String tuKhoa,
            @RequestParam(name = "specialtyId", required = false) Long maChuyenKhoa,
            @RequestParam(name = "academic", required = false) List<String> hocHamHocVi,
            @RequestParam(name = "exp", defaultValue = "all") String kinhNghiem,
            @RequestParam(name = "dateFrom", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(name = "dateTo", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) BigDecimal maxPrice, 
            @RequestParam(required = false) Boolean bhyt,        
            @RequestParam(required = false) List<String> slot,
            @RequestParam(name = "slot", required = false) List<String> khungGio,
            @RequestParam(name = "sort", defaultValue = "name") String sapXep,
            @RequestParam(name = "page", defaultValue = "0") int trang,
            @RequestParam(name = "size", defaultValue = "8") int kichThuoc) {

        return ResponseEntity.ok(bacSiService.timKiemBacSi(
                tuKhoa, maChuyenKhoa, hocHamHocVi, kinhNghiem,
                tuNgay, denNgay, khungGio, sapXep, trang, kichThuoc));
    }

    // Lich lam viec sap toi cua nhieu bac si: /api/doctors/schedules?ids=1,2,3
    @GetMapping("/schedules")
    public ResponseEntity<?> layLichSapToi(
            @RequestParam(name = "ids", required = false) List<Long> danhSachMa) {
        return ResponseEntity.ok(bacSiService.layLichSapToi(danhSachMa));
    }

    // Lay danh sach bac si noi bat
    @GetMapping("/featured")
    public ResponseEntity<?> layBacSiNoiBat() {
        return ResponseEntity.ok(bacSiService.layBacSiNoiBat());
    }

    // Lay chi tiet bac si
    @GetMapping("/{ma}")
    public ResponseEntity<?> layBacSiTheoMa(@PathVariable("ma") Long maBacSi) {
        BacSi bacSi = bacSiService.layBacSiTheoMa(maBacSi);

        if (bacSi == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bacSi);
    }
}
