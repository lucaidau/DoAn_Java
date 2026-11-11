package com.example.doanjava.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.LichLamViec;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.LichLamViecRepository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class BacSiService {

    private final BacSiRepository bacSiRepository;
    private final LichLamViecRepository lichLamViecRepository;
    private final ObjectMapper lichMapper;

    public BacSiService(BacSiRepository bacSiRepository, LichLamViecRepository lichLamViecRepository, ObjectMapper objectMapper) {
        this.bacSiRepository = bacSiRepository;
        this.lichLamViecRepository = lichLamViecRepository;
        this.lichMapper = objectMapper;
    }

    // Tim kiem, loc va phan trang danh sach bac si
    public Page<BacSi> timKiemBacSi(
            String tuKhoa, Long maChuyenKhoa,
            List<String> hocHamHocVi, String kinhNghiem,
            LocalDate tuNgay, LocalDate denNgay, List<String> khungGio,
            String sapXep, int trang, int kichThuoc) {

        Sort sapXepDuLieu;
        if ("exp".equalsIgnoreCase(sapXep)) {
            sapXepDuLieu = Sort.by(Sort.Direction.DESC, "soNamKinhNghiem")
                    .and(Sort.by(Sort.Direction.ASC, "hoTen"));
        } else {
            sapXepDuLieu = Sort.by(Sort.Direction.ASC, "hoTen");
        }

        Pageable phanTrang = PageRequest.of(
                Math.max(0, trang),
                Math.max(1, Math.min(kichThuoc, 100)),
                sapXepDuLieu);

        Specification<BacSi> dieuKien = (root, query, cb) -> cb.conjunction();

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            String tuKhoaTim = "%" + tuKhoa.trim().toLowerCase() + "%";
            dieuKien = dieuKien.and((root, query, cb) -> cb.like(cb.lower(root.get("hoTen")), tuKhoaTim));
        }

        if (maChuyenKhoa != null) {
            dieuKien = dieuKien.and((root, query, cb) -> cb.equal(root.get("chuyenKhoa").get("id"), maChuyenKhoa));
        }

        if (kinhNghiem != null) {
            switch (kinhNghiem) {
                case "under5" ->
                    dieuKien = dieuKien.and((root, query, cb) -> cb.lessThan(root.get("soNamKinhNghiem"), 5));
                case "5-10" ->
                    dieuKien = dieuKien.and((root, query, cb) -> cb.between(root.get("soNamKinhNghiem"), 5, 10));
                case "over10" ->
                    dieuKien = dieuKien.and((root, query, cb) -> cb.greaterThan(root.get("soNamKinhNghiem"), 10));
                case "over15" ->
                    dieuKien = dieuKien.and((root, query, cb) -> cb.greaterThan(root.get("soNamKinhNghiem"), 15));
                default -> {}
            }
        }

        if (hocHamHocVi != null && !hocHamHocVi.isEmpty()) {
            List<String> tuKhoaHocVi = new ArrayList<>();
            for (String muc : hocHamHocVi) {
                switch (muc) {
                    case "gs-pgs" -> {
                        tuKhoaHocVi.add("%gs%");
                        tuKhoaHocVi.add("%giáo sư%");
                    }
                    case "ts" -> {
                        tuKhoaHocVi.add("%ts%");
                        tuKhoaHocVi.add("%tiến sĩ%");
                    }
                    case "ths-ck" -> {
                        tuKhoaHocVi.add("%ths%");
                        tuKhoaHocVi.add("%thạc sĩ%");
                        tuKhoaHocVi.add("%ck%");
                    }
                    default -> tuKhoaHocVi.add("%" + muc.toLowerCase() + "%");
                }
            }

            dieuKien = dieuKien.and((root, query, cb) -> {
                var dieuKienHocVi = cb.disjunction();
                for (String mau : tuKhoaHocVi) {
                    dieuKienHocVi = cb.or(dieuKienHocVi,
                            cb.like(cb.lower(root.get("hocHamHocVi")), mau));
                }
                return dieuKienHocVi;
            });
        }

        boolean locNgay = tuNgay != null || denNgay != null;
        boolean locGio = khungGio != null && !khungGio.isEmpty();
        if (locNgay || locGio) {
            LocalDate tu;
            LocalDate den;
            if (locNgay) {
                tu = tuNgay != null ? tuNgay : denNgay;
                den = denNgay != null ? denNgay : tuNgay;
                if (tu.isAfter(den)) {
                    LocalDate tam = tu;
                    tu = den;
                    den = tam;
                }
            } else {
                tu = LocalDate.now();
                den = tu.plusYears(1);
            }

            List<Long> danhSachMaBacSi = layMaBacSiTheoLich(tu, den, locGio ? khungGio : null);
            if (danhSachMaBacSi.isEmpty()) {
                return Page.empty(phanTrang);
            }

            dieuKien = dieuKien.and((root, query, cb) -> root.get("id").in(danhSachMaBacSi));
        }

        return bacSiRepository.findAll(dieuKien, phanTrang);
    }

    private List<Long> layMaBacSiTheoLich(LocalDate tu, LocalDate den, List<String> khungGio) {
        List<Long> ketQua = new ArrayList<>();
        for (LichLamViec lich : lichLamViecRepository.findByNgayLamViecBetween(tu, den)) {
            if (lich.getBacSi() == null) {
                continue;
            }
            if (khungGio != null) {
                int gio = layGio(chuyenThanhMap(lich).get("gioBatDau"));
                if (gio < 0 || !khopKhungGio(gio, khungGio)) {
                    continue;
                }
            }
            Long ma = lich.getBacSi().getId();
            if (!ketQua.contains(ma)) {
                ketQua.add(ma);
            }
        }
        return ketQua;
    }

    private static boolean khopKhungGio(int gio, List<String> khungGio) {
        for (String muc : khungGio) {
            switch (muc) {
                case "morning" -> { if (gio < 12) return true; }
                case "afternoon" -> { if (gio >= 12 && gio < 17) return true; }
                case "evening" -> { if (gio >= 17) return true; }
                default -> {}
            }
        }
        return false;
    }

    private static int layGio(Object giaTri) {
        if (giaTri == null) {
            return -1;
        }
        if (giaTri instanceof List<?> danhSach && !danhSach.isEmpty()) {
            return ((Number) danhSach.get(0)).intValue();
        }
        try {
            return LocalTime.parse(String.valueOf(giaTri)).getHour();
        } catch (Exception loi) {
            return -1;
        }
    }

    private Map<String, Object> chuyenThanhMap(LichLamViec lich) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", lich.getId());
        map.put("ngayLamViec", lich.getNgayLamViec());
        map.put("gioBatDau", lich.getGioBatDau());
        map.put("gioKetThuc", lich.getGioKetThuc());
        return map;
    }

    public Map<Long, List<Map<String, Object>>> layLichSapToi(List<Long> danhSachMa) {
        Map<Long, List<Map<String, Object>>> ketQua = new LinkedHashMap<>();
        if (danhSachMa == null || danhSachMa.isEmpty()) {
            return ketQua;
        }

        LocalDate homNay = LocalDate.now();
        for (LichLamViec lich : lichLamViecRepository.findByNgayLamViecBetween(homNay, homNay.plusDays(60))) {
            if (lich.getBacSi() == null || !danhSachMa.contains(lich.getBacSi().getId())) {
                continue;
            }
            ketQua.computeIfAbsent(lich.getBacSi().getId(), k -> new ArrayList<>()).add(chuyenThanhMap(lich));
        }

        for (List<Map<String, Object>> danhSach : ketQua.values()) {
            danhSach.sort((Map<String, Object> a, Map<String, Object> b) ->
                (String.valueOf(a.get("ngayLamViec")) + a.get("gioBatDau"))
                .compareTo(String.valueOf(b.get("ngayLamViec")) + b.get("gioBatDau"))
            );
            
            if (danhSach.size() > 4) {
                danhSach.subList(4, danhSach.size()).clear();
            }
        }
        return ketQua;
    }

    public BacSi layBacSiTheoMa(Long maBacSi) {
        return bacSiRepository.findById(maBacSi).orElse(null);
    }

    public List<BacSi> layBacSiNoiBat() {
        return bacSiRepository.findTop8ByOrderBySoNamKinhNghiemDesc();
    }
}