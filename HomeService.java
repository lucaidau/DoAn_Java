package com.example.doanjava.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.LichLamViec;
import com.example.doanjava.repository.BacSiJpa;
import com.example.doanjava.repository.BenhNhanJpa;
import com.example.doanjava.repository.ChuyenKhoaJpa;
import com.example.doanjava.repository.LichLamViecJpa;



@Service
public class HomeService {

    private final ChuyenKhoaJpa chuyenKhoaJpa;
    private final BacSiJpa bacSiJpa;
    private final BenhNhanJpa benhNhanJpa;
    private final LichLamViecJpa lichLamViecJpa;
    private final ChuyenKhoaService chuyenKhoaService;

    public HomeService(ChuyenKhoaJpa chuyenKhoaJpa,
                       BacSiJpa bacSiJpa,
                       BenhNhanJpa benhNhanJpa,
                       LichLamViecJpa lichLamViecJpa,
                       ChuyenKhoaService chuyenKhoaService) {
        this.chuyenKhoaJpa = chuyenKhoaJpa;
        this.bacSiJpa = bacSiJpa;
        this.benhNhanJpa = benhNhanJpa;
        this.lichLamViecJpa = lichLamViecJpa;
        this.chuyenKhoaService = chuyenKhoaService;
    }

    // Lay du lieu hien thi tren trang chu (toan bo lay tu database)
    public Map<String, Object> layDuLieuTrangChu() {
        Map<String, Object> duLieu = new LinkedHashMap<>();

        Map<String, Object> thongKe = new LinkedHashMap<>();
        thongKe.put("soBacSi", bacSiJpa.count());
        thongKe.put("soChuyenKhoa", chuyenKhoaJpa.count());
        thongKe.put("soBenhNhan", benhNhanJpa.count());

        duLieu.put("thongKe", thongKe);
        duLieu.put("chuyenKhoa", chuyenKhoaJpa.findAll());
        duLieu.put("thongKeChuyenKhoa", chuyenKhoaService.thongKeTheoChuyenKhoa());
        duLieu.put("bacSiNoiBat", bacSiJpa.findTop8ByOrderBySoNamKinhNghiemDesc());

        return duLieu;
    }

    // Tim bac si tu trang chu theo tu khoa, chuyen khoa va ngay kham
    public Map<String, Object> timKiemTrangChu(
            String tuKhoa, Long maChuyenKhoa, LocalDate ngayKham) {

        List<BacSi> danhSachBacSi;

        if (ngayKham != null) {
            List<LichLamViec> lichLamViec = lichLamViecJpa.findByNgayLamViec(ngayKham);
            List<Long> danhSachMaBacSi = lichLamViec.stream()
                    .map(lich -> lich.getBacSi().getId())
                    .distinct()
                    .collect(Collectors.toList());

            danhSachBacSi = danhSachMaBacSi.isEmpty()
                    ? new ArrayList<>()
                    : bacSiJpa.findByIdIn(danhSachMaBacSi);
        } else {
            danhSachBacSi = bacSiJpa.findAll();
        }

        final String tuKhoaTim = tuKhoa == null ? "" : tuKhoa.trim().toLowerCase();

        danhSachBacSi = danhSachBacSi.stream()
                .filter(bacSi -> tuKhoaTim.isEmpty()
                        || (bacSi.getHoTen() != null
                            && bacSi.getHoTen().toLowerCase().contains(tuKhoaTim)))
                .filter(bacSi -> maChuyenKhoa == null
                        || (bacSi.getChuyenKhoa() != null
                            && maChuyenKhoa.equals(bacSi.getChuyenKhoa().getId())))
                .toList();

        Map<String, Object> ketQua = new LinkedHashMap<>();
        ketQua.put("bacSi", danhSachBacSi);
        ketQua.put("chuyenKhoa", chuyenKhoaJpa.findAll());

        return ketQua;
    }
}
