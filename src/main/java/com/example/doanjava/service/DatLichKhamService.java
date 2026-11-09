package com.example.doanjava.service;

import com.example.doanjava.dto.ThongTinDatLich;
import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.entity.LichHen;
import com.example.doanjava.entity.LichLamViec;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.BenhNhanRepository;
import com.example.doanjava.repository.LichHenRepository;
import com.example.doanjava.repository.LichLamViecRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DatLichKhamService {
    private static final DateTimeFormatter DINH_DANG_GIO = DateTimeFormatter.ofPattern("HH:mm");

    private final BacSiRepository bacSiRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final LichLamViecRepository lichLamViecRepository;
    private final LichHenRepository lichHenRepository;

    public DatLichKhamService(BacSiRepository bacSiRepository,
                              BenhNhanRepository benhNhanRepository,
                              LichLamViecRepository lichLamViecRepository,
                              LichHenRepository lichHenRepository) {
        this.bacSiRepository = bacSiRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.lichLamViecRepository = lichLamViecRepository;
        this.lichHenRepository = lichHenRepository;
    }

    @Transactional(readOnly = true)
    public BacSi layBacSi(Long id) {
        return bacSiRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public LichLamViec layCaLamViec(Long id) {
        return lichLamViecRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<LichLamViec> layDanhSachCa(Long bacSiId) {
        return lichLamViecRepository
                .findByBacSi_IdAndNgayLamViecGreaterThanEqualOrderByNgayLamViecAscGioBatDauAsc(
                        bacSiId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<String> layKhungGioConTrong(Long lichLamViecId) {
        LichLamViec ca = lichLamViecRepository.findById(lichLamViecId).orElse(null);
        if (ca == null || ca.getNgayLamViec().isBefore(LocalDate.now())) {
            return List.of();
        }

        List<LichHen> lichDaDat = lichHenRepository
                .findByLichLamViec_IdAndTrangThaiNot(lichLamViecId, "DA_HUY");
        Set<String> khungGioDaDat = new HashSet<>();
        for (LichHen lich : lichDaDat) {
            khungGioDaDat.add(lich.getKhungGioKham());
        }

        List<String> khungGioConTrong = new ArrayList<>();
        LocalTime batDau = ca.getGioBatDau();
        LocalTime ketThuc = ca.getGioKetThuc();
        LocalTime hienTai = LocalTime.now();

        while (!batDau.plusMinutes(30).isAfter(ketThuc)) {
            LocalTime gioKetThuc = batDau.plusMinutes(30);
            String khungGio = batDau.format(DINH_DANG_GIO)
                    + " - " + gioKetThuc.format(DINH_DANG_GIO);

            boolean chuaDat = !khungGioDaDat.contains(khungGio);
            boolean chuaQuaGio = !ca.getNgayLamViec().equals(LocalDate.now())
                    || batDau.isAfter(hienTai);

            if (chuaDat && chuaQuaGio) {
                khungGioConTrong.add(khungGio);
            }
            batDau = gioKetThuc;
        }
        return khungGioConTrong;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long taoLichHen(Long benhNhanId, ThongTinDatLich thongTin) {
        if (benhNhanId == null || thongTin == null
                || thongTin.getBacSiId() == null
                || thongTin.getLichLamViecId() == null
                || thongTin.getNgayKham() == null
                || thongTin.getKhungGioKham() == null
                || thongTin.getKhungGioKham().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin đặt lịch.");
        }

        BenhNhan benhNhan = benhNhanRepository.findById(benhNhanId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ bệnh nhân."));
        BacSi bacSi = bacSiRepository.findById(thongTin.getBacSiId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ."));
        LichLamViec ca = lichLamViecRepository.findById(thongTin.getLichLamViecId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca làm việc."));

        if (!ca.getBacSi().getId().equals(bacSi.getId())
                || !ca.getNgayLamViec().equals(thongTin.getNgayKham())) {
            throw new IllegalArgumentException("Ca làm việc không thuộc bác sĩ hoặc ngày khám đã chọn.");
        }
        if (thongTin.getNgayKham().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Không thể đặt lịch vào ngày đã qua.");
        }
        if (!layKhungGioConTrong(ca.getId()).contains(thongTin.getKhungGioKham())) {
            throw new IllegalArgumentException("Khung giờ này vừa được đặt hoặc không còn trống. Vui lòng chọn giờ khác.");
        }

        long soLichDaDat = lichHenRepository
                .countByLichLamViec_IdAndTrangThaiNot(ca.getId(), "DA_HUY");
        int soLuongToiDa = ca.getSoLuongToida() == null ? 10 : ca.getSoLuongToida();
        if (soLichDaDat >= soLuongToiDa) {
            throw new IllegalArgumentException("Ca khám đã đủ số lượng tiếp nhận.");
        }

        LichHen lichHen = new LichHen();
        lichHen.setBenhNhan(benhNhan);
        lichHen.setBacSi(bacSi);
        lichHen.setLichLamViec(ca);
        lichHen.setNgayKham(ca.getNgayLamViec());
        lichHen.setKhungGioKham(thongTin.getKhungGioKham());
        lichHen.setTrieuChungBenh(thongTin.getTrieuChung() == null ? "" : thongTin.getTrieuChung().trim());
        lichHen.setTrangThai("CHO_XAC_NHAN");
        return lichHenRepository.save(lichHen).getId();
    }

    @Transactional(readOnly = true)
    public List<LichHen> layDanhSachLichHenCuaBenhNhan(Long benhNhanId) {
        return lichHenRepository.findByBenhNhan_IdOrderByNgayKhamDesc(benhNhanId);
    }

    @Transactional(readOnly = true)
    public LichHen layLichHenCuaBenhNhan(Long lichHenId, Long benhNhanId) {
        return lichHenRepository.findByIdAndBenhNhan_Id(lichHenId, benhNhanId).orElse(null);
    }
}
