package com.example.doanjava.service;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.ChuyenKhoa;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.ChuyenKhoaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChuyenKhoaService {
    private final ChuyenKhoaRepository chuyenKhoaRepository;
    private final BacSiRepository bacSiRepository;
    private final HomeService homeService;

    public ChuyenKhoaService(ChuyenKhoaRepository chuyenKhoaRepository,
                             BacSiRepository bacSiRepository,
                             HomeService homeService) {
        this.chuyenKhoaRepository = chuyenKhoaRepository;
        this.bacSiRepository = bacSiRepository;
        this.homeService = homeService;
    }

    public List<Map<String, Object>> layDanhSachChuyenKhoa() {
        return chuyenKhoaRepository.findAll(Sort.by(Sort.Direction.ASC, "tenChuyenKhoa"))
                .stream().map(homeService::chuyenKhoaThanhMap).toList();
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
