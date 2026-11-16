package com.example.doanjava.service;

import com.example.doanjava.entity.LichLamViec;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.ChuyenKhoaRepository;
import com.example.doanjava.repository.LichLamViecRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BacSiService {
    private final BacSiRepository bacSiRepository;
    private final ChuyenKhoaRepository chuyenKhoaRepository;
    private final LichLamViecRepository lichLamViecRepository;
    private final HomeService homeService;

    public BacSiService(BacSiRepository bacSiRepository,
                        ChuyenKhoaRepository chuyenKhoaRepository,
                        LichLamViecRepository lichLamViecRepository,
                        HomeService homeService) {
        this.bacSiRepository = bacSiRepository;
        this.chuyenKhoaRepository = chuyenKhoaRepository;
        this.lichLamViecRepository = lichLamViecRepository;
        this.homeService = homeService;
    }

    public List<Map<String, Object>> layDanhSachBacSi() {
        return bacSiRepository.findAll(Sort.by(Sort.Direction.ASC, "hoTen"))
                .stream().map(homeService::bacSiThanhMap).toList();
    }

    public List<Map<String, Object>> layDanhSachChuyenKhoa() {
        return chuyenKhoaRepository.findAll(Sort.by(Sort.Direction.ASC, "tenChuyenKhoa"))
                .stream().map(homeService::chuyenKhoaThanhMap).toList();
    }

    public Map<Long, List<Map<String, Object>>> layLichSapToi() {
        LocalDate homNay = LocalDate.now();
        List<LichLamViec> lichLamViecs = lichLamViecRepository
                .findByNgayLamViecBetween(homNay, homNay.plusMonths(6));

        Map<Long, List<Map<String, Object>>> ketQua = new LinkedHashMap<>();
        for (LichLamViec lich : lichLamViecs) {
            if (lich.getBacSi() == null || lich.getBacSi().getId() == null) continue;
            Map<String, Object> dongLich = new LinkedHashMap<>();
            dongLich.put("ngayLamViec", lich.getNgayLamViec());
            dongLich.put("tenCa", lich.getTenCa());
            dongLich.put("gioBatDau", lich.getGioBatDau());
            dongLich.put("gioKetThuc", lich.getGioKetThuc());
            ketQua.computeIfAbsent(lich.getBacSi().getId(), id -> new ArrayList<>()).add(dongLich);
        }
        return ketQua;
    }

    public Map<Long, Map<String, Object>> layThongKeChuyenKhoa() {
        return homeService.layThongKeChuyenKhoa();
    }
}
