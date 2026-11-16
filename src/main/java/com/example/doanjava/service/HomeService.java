package com.example.doanjava.service;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.ChuyenKhoa;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.BenhNhanRepository;
import com.example.doanjava.repository.ChuyenKhoaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeService {
    private final BacSiRepository bacSiRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final ChuyenKhoaRepository chuyenKhoaRepository;

    public HomeService(BacSiRepository bacSiRepository,
                       BenhNhanRepository benhNhanRepository,
                       ChuyenKhoaRepository chuyenKhoaRepository) {
        this.bacSiRepository = bacSiRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.chuyenKhoaRepository = chuyenKhoaRepository;
    }

    public Map<String, Long> layThongKe() {
        Map<String, Long> ketQua = new LinkedHashMap<>();
        ketQua.put("soBacSi", bacSiRepository.count());
        ketQua.put("soBenhNhan", benhNhanRepository.count());
        ketQua.put("soChuyenKhoa", chuyenKhoaRepository.count());
        return ketQua;
    }

    public List<Map<String, Object>> layChuyenKhoaNoiBat() {
        return chuyenKhoaRepository.findAll(Sort.by(Sort.Direction.ASC, "tenChuyenKhoa"))
                .stream().map(this::chuyenKhoaThanhMap).toList();
    }

    public List<Map<String, Object>> layBacSiNoiBat() {
        return bacSiRepository.findTop8ByOrderBySoNamKinhNghiemDesc()
                .stream().map(this::bacSiThanhMap).toList();
    }

    public Map<Long, Map<String, Object>> layThongKeChuyenKhoa() {
        Map<Long, Map<String, Object>> ketQua = new LinkedHashMap<>();
        List<BacSi> tatCaBacSi = bacSiRepository.findAll();
        for (ChuyenKhoa chuyenKhoa : chuyenKhoaRepository.findAll()) {
            List<BacSi> bacSis = tatCaBacSi.stream()
                    .filter(bs -> bs.getChuyenKhoa() != null
                            && chuyenKhoa.getId().equals(bs.getChuyenKhoa().getId())).toList();
            Map<String, Object> thongKe = new LinkedHashMap<>();
            thongKe.put("soBacSi", bacSis.size());
            BacSi bacSiDaiDien = bacSis.stream()
                    .max((a, b) -> Integer.compare(a.getSoNamKinhNghiem() == null ? 0 : a.getSoNamKinhNghiem(),
                            b.getSoNamKinhNghiem() == null ? 0 : b.getSoNamKinhNghiem())).orElse(null);
            thongKe.put("bacSiPhuTrach", bacSiDaiDien == null ? null : bacSiThanhMap(bacSiDaiDien));
            ketQua.put(chuyenKhoa.getId(), thongKe);
        }
        return ketQua;
    }

    public Map<String, Object> chuyenKhoaThanhMap(ChuyenKhoa ck) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", ck.getId());
        map.put("tenChuyenKhoa", ck.getTenChuyenKhoa());
        map.put("moTa", ck.getMoTa());
        return map;
    }

    public Map<String, Object> bacSiThanhMap(BacSi bs) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", bs.getId());
        map.put("hoTen", bs.getHoTen());
        map.put("hocHamHocVi", bs.getHocHamHocVi());
        map.put("soNamKinhNghiem", bs.getSoNamKinhNghiem());
        map.put("gioiThieu", bs.getGioiThieu());
        map.put("chuyenKhoa", bs.getChuyenKhoa() == null ? null : chuyenKhoaThanhMap(bs.getChuyenKhoa()));
        return map;
    }
}
