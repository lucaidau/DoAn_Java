package com.example.doanjava.service;

import com.example.doanjava.dto.AppointmentForm;
import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.entity.LichHen;
import com.example.doanjava.entity.LichLamViec;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.BenhNhanRepository;
import com.example.doanjava.repository.LichHenRepository;
import com.example.doanjava.repository.LichLamViecRepository;
import com.example.doanjava.repository.TaiKhoanRepository;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientBookingService {
    private static final DateTimeFormatter GIO_HIEN_THI = DateTimeFormatter.ofPattern("HH:mm");
    private static final String DA_HUY = "DA_HUY";

    private final BacSiRepository bacSiRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final LichLamViecRepository lichLamViecRepository;
    private final LichHenRepository lichHenRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final JdbcTemplate jdbcTemplate;

    public PatientBookingService(
            BacSiRepository bacSiRepository,
            BenhNhanRepository benhNhanRepository,
            LichLamViecRepository lichLamViecRepository,
            LichHenRepository lichHenRepository,
            TaiKhoanRepository taiKhoanRepository,
            JdbcTemplate jdbcTemplate) {
        this.bacSiRepository = bacSiRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.lichLamViecRepository = lichLamViecRepository;
        this.lichHenRepository = lichHenRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public BenhNhan layBenhNhanDangNhap(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập."));

        return benhNhanRepository.findFirstByTaiKhoanId(taiKhoan.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản này chưa có hồ sơ bệnh nhân."));
    }

    @Transactional(readOnly = true)
    public List<BacSi> timBacSi(String keyword) {
        List<BacSi> danhSach = bacSiRepository.findAll(Sort.by("hoTen").ascending());
        if (keyword == null || keyword.isBlank()) {
            return danhSach.stream()
                    .filter(bacSi -> bacSi.getTaiKhoan() == null || Boolean.TRUE.equals(bacSi.getTaiKhoan().getTrangThai()))
                    .toList();
        }

        String tuKhoa = keyword.trim().toLowerCase(Locale.ROOT);
        return danhSach.stream()
                .filter(bacSi -> bacSi.getTaiKhoan() == null || Boolean.TRUE.equals(bacSi.getTaiKhoan().getTrangThai()))
                .filter(bacSi -> containsIgnoreCase(bacSi.getHoTen(), tuKhoa)
                        || containsIgnoreCase(bacSi.getHocHamHocVi(), tuKhoa)
                        || (bacSi.getChuyenKhoa() != null
                        && containsIgnoreCase(bacSi.getChuyenKhoa().getTenChuyenKhoa(), tuKhoa)))
                .toList();
    }

    @Transactional(readOnly = true)
    public BacSi layBacSi(Long bacSiId) {
        if (bacSiId == null) {
            throw new IllegalArgumentException("Bạn chưa chọn bác sĩ.");
        }
        return bacSiRepository.findById(bacSiId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ."));
    }

    @Transactional(readOnly = true)
    public LichLamViec layCaKham(Long bacSiId, Long lichLamViecId) {
        if (lichLamViecId == null) {
            throw new IllegalArgumentException("Bạn chưa chọn ca khám.");
        }
        LichLamViec lich = lichLamViecRepository.findById(lichLamViecId)
                .orElseThrow(() -> new IllegalArgumentException("Ca khám không tồn tại."));
        if (lich.getBacSi() == null || !Objects.equals(lich.getBacSi().getId(), bacSiId)) {
            throw new IllegalArgumentException("Ca khám không thuộc bác sĩ đã chọn.");
        }
        return lich;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> thongTinBacSi(BacSi bacSi) {
        Map<String, Object> doctor = new HashMap<>();
        doctor.put("doctorId", bacSi.getId());
        doctor.put("doctorName", bacSi.getHoTen());
        doctor.put("degree", bacSi.getHocHamHocVi() == null ? "Bác sĩ" : bacSi.getHocHamHocVi());
        doctor.put("experience", bacSi.getSoNamKinhNghiem() == null ? 0 : bacSi.getSoNamKinhNghiem());
        doctor.put("introduction", bacSi.getGioiThieu() == null ? "Thông tin giới thiệu đang được cập nhật." : bacSi.getGioiThieu());
        String tenChuyenKhoa = bacSi.getChuyenKhoa() == null
                ? "Chưa cập nhật chuyên khoa" : bacSi.getChuyenKhoa().getTenChuyenKhoa();
        doctor.put("specialty", tenChuyenKhoa);
        doctor.put("phone", bacSi.getTaiKhoan() == null || bacSi.getTaiKhoan().getSoDienThoai() == null
                ? "" : bacSi.getTaiKhoan().getSoDienThoai());
        doctor.put("email", bacSi.getTaiKhoan() == null || bacSi.getTaiKhoan().getEmail() == null
                ? "" : bacSi.getTaiKhoan().getEmail());
        doctor.put("consultationFee", layGiaKhamTheoChuyenKhoa(tenChuyenKhoa));
        return doctor;
    }

    /**
     * Đọc mức phí từ danh mục mở rộng dbo.dich_vu_kham nếu database đã có bảng này.
     * Database SQL gốc không bắt buộc phải có bảng dịch vụ; khi chưa có bảng/giá,
     * trả về null để giao diện hiển thị "Chưa cập nhật" thay vì tự dựng giá.
     */
    private BigDecimal layGiaKhamTheoChuyenKhoa(String tenChuyenKhoa) {
        String tenDichVu = tenDichVuTheoChuyenKhoa(tenChuyenKhoa);
        if (tenDichVu == null) {
            return null;
        }

        try {
            Integer bangTonTai = jdbcTemplate.queryForObject(
                    "SELECT CASE WHEN OBJECT_ID(N'dbo.dich_vu_kham', N'U') IS NULL THEN 0 ELSE 1 END",
                    Integer.class);
            if (!Integer.valueOf(1).equals(bangTonTai)) {
                return null;
            }

            List<BigDecimal> ketQua = jdbcTemplate.query(
                    "SELECT TOP (1) gia_kham FROM dbo.dich_vu_kham " +
                            "WHERE ten_dich_vu = ? AND gia_kham IS NOT NULL ORDER BY id",
                    (rs, rowNum) -> rs.getBigDecimal("gia_kham"),
                    tenDichVu);
            return ketQua.isEmpty() ? null : ketQua.get(0);
        } catch (DataAccessException ex) {
            // Không làm hỏng trang hồ sơ bác sĩ nếu database chỉ dùng schema SQL gốc.
            return null;
        }
    }

    private String tenDichVuTheoChuyenKhoa(String tenChuyenKhoa) {
        if (tenChuyenKhoa == null) {
            return null;
        }
        String ten = tenChuyenKhoa.toLowerCase(Locale.ROOT);
        if (ten.contains("nội tổng quát")) return "Khám Nội tổng quát";
        if (ten.contains("tim mạch")) return "Khám Tim mạch";
        if (ten.contains("nhi")) return "Khám Nhi";
        if (ten.contains("tai mũi họng")) return "Khám Tai Mũi Họng";
        return null;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> lichSapToi(Long bacSiId) {
        LocalDate homNay = LocalDate.now();
        return lichLamViecRepository
                .findByBacSi_IdAndNgayLamViecGreaterThanEqualOrderByNgayLamViecAscGioBatDauAsc(bacSiId, homNay)
                .stream()
                .filter(lich -> !khungGioConTrong(lich).isEmpty())
                .map(this::thongTinCaKham)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> thongTinCaKham(LichLamViec lich) {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("scheduleId", lich.getId());
        schedule.put("workDate", Date.valueOf(lich.getNgayLamViec()));
        schedule.put("shiftName", lich.getTenCa());
        schedule.put("startTime", Time.valueOf(lich.getGioBatDau()));
        schedule.put("endTime", Time.valueOf(lich.getGioKetThuc()));
        schedule.put("maxPatients", lich.getSoLuongToida());
        schedule.put("ngay_lam_viec", Date.valueOf(lich.getNgayLamViec()));
        schedule.put("ten_ca", lich.getTenCa());
        schedule.put("gio_bat_dau", Time.valueOf(lich.getGioBatDau()));
        schedule.put("gio_ket_thuc", Time.valueOf(lich.getGioKetThuc()));
        return schedule;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> thongTinBenhNhan(BenhNhan benhNhan) {
        Map<String, Object> patient = new HashMap<>();
        patient.put("patientId", benhNhan.getId());
        patient.put("fullName", benhNhan.getHoTen());
        patient.put("birthDate", benhNhan.getNgaySinh() == null ? null : Date.valueOf(benhNhan.getNgaySinh()));
        patient.put("gender", benhNhan.getGioiTinh());
        patient.put("phone", benhNhan.getTaiKhoan() == null ? "" : benhNhan.getTaiKhoan().getSoDienThoai());
        patient.put("address", benhNhan.getDiaChi());
        return patient;
    }

    @Transactional(readOnly = true)
    public List<String> khungGioConTrong(LichLamViec lich) {
        LocalDate ngayKham = lich.getNgayLamViec();
        if (ngayKham == null || ngayKham.isBefore(LocalDate.now())) {
            return List.of();
        }

        List<LichHen> daDat = lichHenRepository.findByLichLamViec_IdAndTrangThaiNot(lich.getId(), DA_HUY);
        Set<String> khungGioDaDat = daDat.stream()
                .map(LichHen::getKhungGioKham)
                .collect(Collectors.toSet());

        int gioiHan = lich.getSoLuongToida() == null ? 0 : lich.getSoLuongToida();
        if (gioiHan <= 0 || daDat.size() >= gioiHan) {
            return List.of();
        }

        LocalTime batDau = lich.getGioBatDau();
        LocalTime ketThuc = lich.getGioKetThuc();
        if (batDau == null || ketThuc == null || !batDau.isBefore(ketThuc)) {
            return List.of();
        }

        List<String> khungGio = new ArrayList<>();
        LocalTime hienTai = LocalTime.now();
        while (!batDau.plusMinutes(30).isAfter(ketThuc)) {
            LocalTime ketThucSlot = batDau.plusMinutes(30);
            String slot = GIO_HIEN_THI.format(batDau) + " - " + GIO_HIEN_THI.format(ketThucSlot);
            boolean chuaQuaGio = !ngayKham.equals(LocalDate.now()) || batDau.isAfter(hienTai);
            if (chuaQuaGio && !khungGioDaDat.contains(slot)) {
                khungGio.add(slot);
            }
            batDau = ketThucSlot;
        }
        return khungGio;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long taoLichHen(BenhNhan benhNhan, AppointmentForm form) {
        if (form == null || form.getDoctorId() == null || form.getScheduleId() == null) {
            throw new IllegalArgumentException("Thông tin bác sĩ hoặc ca khám không hợp lệ.");
        }
        if (form.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Ngày khám không hợp lệ. Vui lòng tải lại trang.");
        }
        if (form.getTimeSlot() == null || form.getTimeSlot().isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn một khung giờ khám.");
        }
        if (form.getSymptoms() == null || form.getSymptoms().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập lý do khám hoặc triệu chứng.");
        }
        if (form.getSymptoms().trim().length() > 2000) {
            throw new IllegalArgumentException("Phần mô tả triệu chứng không được vượt quá 2.000 ký tự.");
        }

        LichLamViec lich = lichLamViecRepository.findByIdForUpdate(form.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Ca khám không còn tồn tại."));
        if (lich.getBacSi() == null || !Objects.equals(lich.getBacSi().getId(), form.getDoctorId())) {
            throw new IllegalArgumentException("Ca khám không thuộc bác sĩ đã chọn.");
        }
        if (form.getAppointmentDate() != null && !form.getAppointmentDate().equals(lich.getNgayLamViec())) {
            throw new IllegalArgumentException("Ngày khám đã thay đổi. Vui lòng tải lại trang.");
        }
        if (lich.getNgayLamViec().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Không thể đặt lịch trong quá khứ.");
        }

        List<String> conTrong = khungGioConTrong(lich);
        if (!conTrong.contains(form.getTimeSlot())) {
            throw new IllegalArgumentException("Khung giờ vừa chọn không còn trống. Vui lòng chọn khung giờ khác.");
        }

        LichHen lichHen = new LichHen();
        lichHen.setBenhNhan(benhNhan);
        lichHen.setBacSi(lich.getBacSi());
        lichHen.setLichLamViec(lich);
        lichHen.setNgayKham(lich.getNgayLamViec());
        lichHen.setKhungGioKham(form.getTimeSlot());
        lichHen.setTrieuChungBenh(form.getSymptoms().trim());
        lichHen.setTrangThai("CHO_XAC_NHAN");
        lichHen.setNgayTao(LocalDateTime.now());
        return lichHenRepository.saveAndFlush(lichHen).getId();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> thongTinLichHen(Long appointmentId, Long patientId) {
        LichHen lichHen = lichHenRepository.findByIdAndBenhNhan_Id(appointmentId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn của bệnh nhân này."));

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("appointmentId", lichHen.getId());
        appointment.put("patientName", lichHen.getBenhNhan().getHoTen());
        appointment.put("phone", lichHen.getBenhNhan().getTaiKhoan() == null
                ? "" : lichHen.getBenhNhan().getTaiKhoan().getSoDienThoai());
        appointment.put("doctorName", lichHen.getBacSi().getHoTen());
        appointment.put("specialty", lichHen.getBacSi().getChuyenKhoa() == null
                ? "" : lichHen.getBacSi().getChuyenKhoa().getTenChuyenKhoa());
        appointment.put("appointmentDate", Date.valueOf(lichHen.getNgayKham()));
        appointment.put("timeSlot", lichHen.getKhungGioKham());
        appointment.put("symptoms", lichHen.getTrieuChungBenh());
        appointment.put("status", lichHen.getTrangThai());
        return appointment;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> lichHenCuaBenhNhan(Long patientId) {
        return lichHenRepository.findByBenhNhan_IdOrderByNgayKhamDescKhungGioKhamAsc(patientId)
                .stream()
                .map(lichHen -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", lichHen.getId());
                    row.put("doctorName", lichHen.getBacSi().getHoTen());
                    row.put("specialty", lichHen.getBacSi().getChuyenKhoa() == null
                            ? "" : lichHen.getBacSi().getChuyenKhoa().getTenChuyenKhoa());
                    row.put("date", lichHen.getNgayKham());
                    row.put("time", lichHen.getKhungGioKham());
                    row.put("status", lichHen.getTrangThai());
                    row.put("symptoms", lichHen.getTrieuChungBenh());
                    return row;
                })
                .toList();
    }

    @Transactional
    public void huyLichHen(Long appointmentId, Long patientId) {
        LichHen lichHen = lichHenRepository.findByIdAndBenhNhan_Id(appointmentId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn."));
        if (!List.of("CHO_XAC_NHAN", "DA_XAC_NHAN").contains(lichHen.getTrangThai())) {
            throw new IllegalArgumentException("Lịch hẹn hiện tại không thể hủy.");
        }
        if (lichHen.getNgayKham().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Không thể hủy lịch đã qua ngày khám.");
        }
        lichHen.setTrangThai(DA_HUY);
        lichHenRepository.save(lichHen);
    }

    private boolean containsIgnoreCase(String source, String search) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(search);
    }
}
