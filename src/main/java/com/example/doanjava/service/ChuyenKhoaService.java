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
import org.springframework.transaction.annotation.Transactional;

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

    public List<Map<String, Object>> layDanhSachChuyenKhoaAdmin(String search) {
        List<Map<String, Object>> danhSach = chuyenKhoaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
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

    public ChuyenKhoa themChuyenKhoa(String tenChuyenKhoa, String moTa) {
        if (tenChuyenKhoa == null || tenChuyenKhoa.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chuyên khoa không được để trống!");
        }

        String tenChuan = tenChuyenKhoa.trim();
        String moTaChuan = (moTa != null && !moTa.trim().isEmpty()) ? moTa.trim() : null;

        boolean daTonTai = chuyenKhoaRepository.findAll().stream()
                .anyMatch(ck -> ck.getTenChuyenKhoa() != null
                        && ck.getTenChuyenKhoa().trim().equalsIgnoreCase(tenChuan));
        if (daTonTai) {
            throw new IllegalArgumentException("Chuyên khoa \"" + tenChuan + "\" đã tồn tại!");
        }

        ChuyenKhoa chuyenKhoa = new ChuyenKhoa();
        chuyenKhoa.setTenChuyenKhoa(tenChuyenKhoa);
        chuyenKhoa.setMoTa(moTa); // Đổi tên setter nếu entity ChuyenKhoa dùng tên khác

        return chuyenKhoaRepository.save(chuyenKhoa);
    }

    public List<PhongKham> layDanhSachPhongTrongChoChuyenKhoa(Long chuyenKhoaId) {
        return phongKhamRepository.findAll().stream()
                .filter(pk -> pk != null && (
                        pk.getChuyenKhoa() == null
                                || pk.getChuyenKhoa().getId() == null
                                || (chuyenKhoaId != null && chuyenKhoaId.equals(pk.getChuyenKhoa().getId()))
                ))
                .toList();
    }

    @Transactional
    public void capNhatChuyenKhoa(Long id, String tenChuyenKhoa, String moTa, List<Long> phongKhamIds) {
        if (id == null) {
            throw new IllegalArgumentException("Không tìm thấy ID chuyên khoa cần sửa!");
        }
        if (tenChuyenKhoa == null || tenChuyenKhoa.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chuyên khoa không được để trống!");
        }

        ChuyenKhoa ck = chuyenKhoaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chuyên khoa không tồn tại!"));

        String tenChuan = tenChuyenKhoa.trim();
        boolean trungTen = chuyenKhoaRepository.findAll().stream()
                .anyMatch(item -> !item.getId().equals(id)
                        && item.getTenChuyenKhoa() != null
                        && item.getTenChuyenKhoa().trim().equalsIgnoreCase(tenChuan));
        if (trungTen) {
            throw new IllegalArgumentException("Tên chuyên khoa \"" + tenChuan + "\" đã được sử dụng!");
        }

        ck.setTenChuyenKhoa(tenChuan);
        ck.setMoTa(moTa != null && !moTa.trim().isEmpty() ? moTa.trim() : null); // Đổi tên setter nếu entity dùng tên khác
        chuyenKhoaRepository.save(ck);

        Set<Long> selectedIds = (phongKhamIds != null) ? new HashSet<>(phongKhamIds) : Collections.emptySet();

        for (PhongKham pk : phongKhamRepository.findAll()) {
            Long pkCkId = (pk.getChuyenKhoa() != null) ? pk.getChuyenKhoa().getId() : null;
            boolean dangThuocCkHienTai = id.equals(pkCkId);
            boolean duocChon = pk.getId() != null && selectedIds.contains(pk.getId());

            if (duocChon) {
                pk.setChuyenKhoa(ck);
                phongKhamRepository.save(pk);
            } else if (dangThuocCkHienTai) {
                pk.setChuyenKhoa(null);
                phongKhamRepository.save(pk);
            }
        }
    }
}
