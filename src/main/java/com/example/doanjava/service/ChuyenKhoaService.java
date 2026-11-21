package com.example.doanjava.service;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.ChuyenKhoa;
import com.example.doanjava.entity.PhongKham;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.ChuyenKhoaRepository;
import com.example.doanjava.repository.PhongKhamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChuyenKhoaService {
    private final ChuyenKhoaRepository chuyenKhoaRepository;
    private final BacSiRepository bacSiRepository;
    private final HomeService homeService;
    private final PhongKhamRepository phongKhamRepository;

    public ChuyenKhoaService(ChuyenKhoaRepository chuyenKhoaRepository,
                             BacSiRepository bacSiRepository,
                             HomeService homeService, PhongKhamRepository phongKhamRepository) {
        this.chuyenKhoaRepository = chuyenKhoaRepository;
        this.bacSiRepository = bacSiRepository;
        this.homeService = homeService;
        this.phongKhamRepository = phongKhamRepository;
    }

    public List<Map<String, Object>> layDanhSachChuyenKhoa() {
        return chuyenKhoaRepository.findAll(Sort.by(Sort.Direction.ASC, "tenChuyenKhoa"))
                .stream().map(homeService::chuyenKhoaThanhMap).toList();
    }

    public List<Map<String,Object>> layDanhSachChuyenKhoaAdmin(String search) {
        List<Map<String,Object>> danhSach = chuyenKhoaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(homeService::chuyenKhoaThanhMap)
                .sorted(Comparator.comparingLong(m -> Long.parseLong(m.get("id").toString())))
                .toList();

        if (search == null || search.trim().isEmpty()) {
            return danhSach;
        }

        String tuKhoa = search.trim().toLowerCase();
        return danhSach.stream()
                .filter(ck -> {
                    Object ten = ck.get("tenChuyenKhoa");
                    return ten != null && ten.toString().toLowerCase().contains(tuKhoa);
                })
                .toList();
    }

    public Map<Long, List<PhongKham>> layPhongKhamTheoChuyenKhoa() {
        Map<Long, List<PhongKham>> mapPhong = new HashMap<>();
        List<PhongKham> dsPhong = phongKhamRepository.findAll();

        for (PhongKham pk : dsPhong) {
            if (pk != null && pk.getChuyenKhoa() != null && pk.getChuyenKhoa().getId() != null) {
                Long ckId = pk.getChuyenKhoa().getId();
                mapPhong.computeIfAbsent(ckId, k -> new ArrayList<>()).add(pk);
            }
        }
        return mapPhong;
    }

    public Page<Map<String, Object>> layDanhSachChuyenKhoaAdmin(String search, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size, Sort.by("id").ascending());
        String tuKhoa = (search == null) ? "" : search.trim();

        return chuyenKhoaRepository
                .findByTenChuyenKhoaContainingIgnoreCase(tuKhoa, pageable)
                .map(homeService::chuyenKhoaThanhMap);
    }

    public Map<Long, Map<String, Object>> layThongKeChuyenKhoa() {
        return homeService.layThongKeChuyenKhoa();
    }

    public long demBacSiCoHocHamHocVi() {
        return bacSiRepository.findAll().stream()
                .filter(bs -> bs.getHocHamHocVi() != null && !bs.getHocHamHocVi().isBlank())
                .count();
    }
}
