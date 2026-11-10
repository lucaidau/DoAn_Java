package com.example.doanjava.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.ChuyenKhoa;
import com.example.doanjava.repository.BacSiJpa;
import com.example.doanjava.repository.ChuyenKhoaJpa;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class ChuyenKhoaService {

    private final ChuyenKhoaJpa chuyenKhoaJpa;
    private final BacSiJpa bacSiJpa;
    private final ObjectMapper objectMapper;

    public ChuyenKhoaService(ChuyenKhoaJpa chuyenKhoaJpa, BacSiJpa bacSiJpa,
                             ObjectMapper objectMapper) {
        this.chuyenKhoaJpa = chuyenKhoaJpa;
        this.bacSiJpa = bacSiJpa;
        this.objectMapper = objectMapper;
    }

    // Tim kiem va phan trang chuyen khoa
    public Page<ChuyenKhoa> timKiemChuyenKhoa(
            String tuKhoa, String loaiChuyenKhoa,
            String sapXep, int trang, int kichThuoc) {

        // alpha-desc: Z-A, con lai: A-Z
        Sort.Direction huong = "alpha-desc".equalsIgnoreCase(sapXep)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sapXepDuLieu = Sort.by(huong, "tenChuyenKhoa");

        Pageable phanTrang = PageRequest.of(
                Math.max(0, trang),
                Math.max(1, Math.min(kichThuoc, 100)),
                sapXepDuLieu);

        String tuKhoaTim = tuKhoa == null ? "" : tuKhoa.trim();

        // Database hien tai chua co cot loai_chuyen_khoa nen chua loc category
        if (tuKhoaTim.isEmpty()) {
            return chuyenKhoaJpa.findAll(phanTrang);
        }

        return chuyenKhoaJpa.findByTenChuyenKhoaContainingIgnoreCase(
                tuKhoaTim, phanTrang);
    }

    // Lay chi tiet chuyen khoa theo ma
    public ChuyenKhoa layChuyenKhoaTheoMa(Long maChuyenKhoa) {
        return chuyenKhoaJpa.findById(maChuyenKhoa).orElse(null);
    }

    // Thong ke theo chuyen khoa: { maChuyenKhoa -> { soBacSi, bacSiPhuTrach } }
    // bacSiPhuTrach la bac si co so nam kinh nghiem cao nhat cua chuyen khoa do
    public Map<Long, Map<String, Object>> thongKeTheoChuyenKhoa() {
        Map<Long, Map<String, Object>> ketQua = new LinkedHashMap<>();

        for (BacSi bacSi : bacSiJpa.findAll(Sort.by(Sort.Direction.DESC, "soNamKinhNghiem"))) {
            if (bacSi.getChuyenKhoa() == null) {
                continue;
            }

            Map<String, Object> thongKe = ketQua.computeIfAbsent(
                    bacSi.getChuyenKhoa().getId(), ma -> {
                        Map<String, Object> moi = new LinkedHashMap<>();
                        moi.put("soBacSi", 0L);
                        return moi;
                    });

            thongKe.put("soBacSi", (Long) thongKe.get("soBacSi") + 1);

            if (!thongKe.containsKey("bacSiPhuTrach")) {
                thongKe.put("bacSiPhuTrach", objectMapper.convertValue(
                        bacSi, new TypeReference<Map<String, Object>>() { }));
            }
        }
        return ketQua;
    }
}
