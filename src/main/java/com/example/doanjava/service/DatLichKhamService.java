
package com.example.doanjava.service;

import com.example.doanjava.dto.AppointmentForm;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class DatLichKhamService {

    private final JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter DINH_DANG_GIO =
            DateTimeFormatter.ofPattern("HH:mm");

    public DatLichKhamService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Lấy hồ sơ bệnh nhân đang đăng nhập.
    public Map<String, Object> layBenhNhanDangNhap(String tenDangNhap) {

        String cotCCCD = layBieuThucCotBenhNhan("so_cccd");
        String cotBHYT = layBieuThucCotBenhNhan("ma_bhyt");

        String sql = "SELECT TOP (1) " +
                "bn.id AS id, " +
                "bn.ho_ten AS hoTen, " +
                "bn.ngay_sinh AS birthDate, " +
                "bn.gioi_tinh AS gender, " +
                "bn.dia_chi AS address, " +
                "tk.so_dien_thoai AS phone, " +
                cotCCCD + " AS soCccd, " +
                cotBHYT + " AS maBhyt " +
                "FROM dbo.benh_nhan bn " +
                "INNER JOIN dbo.tai_khoan tk ON tk.id = bn.tai_khoan_id " +
                "WHERE tk.ten_dang_nhap = ? " +
                "AND tk.vai_tro = 'BENH_NHAN' " +
                "AND tk.trang_thai = 1";

        Map<String, Object> benhNhan = layMotDong(
                sql,
                "Tài khoản chưa có hồ sơ bệnh nhân.",
                tenDangNhap);

        String cccd = Objects.toString(
                benhNhan.get("soCccd"), "").trim();

        String bhyt = Objects.toString(
                benhNhan.get("maBhyt"), "").trim();

        benhNhan.put("fullName", benhNhan.get("hoTen"));

        benhNhan.put(
                "giayTo",
                !cccd.isEmpty()
                        ? cccd
                        : !bhyt.isEmpty()
                          ? bhyt
                          : "Chưa cập nhật");

        return benhNhan;
    }

    // Chỉ đọc hai cột giấy tờ nếu database thực sự có chúng.
    private String layBieuThucCotBenhNhan(String tenCot) {

        if (!Set.of("so_cccd", "ma_bhyt").contains(tenCot)) {
            throw new IllegalArgumentException("Tên cột không hợp lệ.");
        }

        String sql = "SELECT CASE WHEN COL_LENGTH(" +
                "'dbo.benh_nhan', '" + tenCot +
                "') IS NULL THEN 0 ELSE 1 END";

        Integer tonTai = jdbcTemplate.queryForObject(
                sql, Integer.class);

        if (tonTai != null && tonTai == 1) {
            return "bn." + tenCot;
        }

        return "CAST(NULL AS VARCHAR(30))";
    }

    // Lấy thông tin bác sĩ.
    public Map<String, Object> layThongTinBacSi(Long bacSiId) {

        String sql = "SELECT " +
                "b.id AS id, " +
                "b.id AS doctorId, " +
                "b.ho_ten AS doctorName, " +
                "b.hoc_ham_hoc_vi AS degree, " +
                "b.so_nam_kinh_nghiem AS experience, " +
                "b.gioi_thieu AS introduction, " +
                "ck.id AS specialtyId, " +
                "ck.ten_chuyen_khoa AS specialty, " +
                "tk.email AS email, " +
                "tk.so_dien_thoai AS phone " +
                "FROM dbo.bac_si b " +
                "INNER JOIN dbo.chuyen_khoa ck ON ck.id = b.chuyen_khoa_id " +
                "INNER JOIN dbo.tai_khoan tk ON tk.id = b.tai_khoan_id " +
                "WHERE b.id = ? AND tk.trang_thai = 1";

        Map<String, Object> bacSi = layMotDong(
                sql,
                "Không tìm thấy bác sĩ hoặc bác sĩ hiện không hoạt động.",
                bacSiId);

        if (bacSi.get("degree") == null) {
            bacSi.put("degree", "Bác sĩ");
        }

        if (bacSi.get("experience") == null) {
            bacSi.put("experience", 0);
        }

        if (bacSi.get("introduction") == null) {
            bacSi.put(
                    "introduction",
                    "Thông tin giới thiệu đang được cập nhật.");
        }

        return bacSi;
    }

    // Lấy lịch làm việc sắp tới.

    public List<Map<String, Object>> layLichSapToi(Long bacSiId) {

        String sql = "SELECT " +
                "llv.id AS scheduleId, " +
                "llv.bac_si_id AS doctorId, " +
                "llv.ngay_lam_viec AS workDate, " +
                "llv.ten_ca AS shiftName, " +
                "llv.gio_bat_dau AS startTime, " +
                "llv.gio_ket_thuc AS endTime, " +
                "llv.so_luong_toida AS maxPatients " +
                "FROM dbo.lich_lam_viec llv " +
                "WHERE llv.bac_si_id = ? " +
                "AND llv.ngay_lam_viec >= ? " +
                "AND llv.ngay_lam_viec <= ? " +
                "ORDER BY llv.ngay_lam_viec, llv.gio_bat_dau";

        List<Map<String, Object>> ketQua =
                jdbcTemplate.queryForList(
                        sql,
                        bacSiId,
                        Date.valueOf(LocalDate.now()),
                        Date.valueOf(LocalDate.now().plusMonths(6)));

        List<Map<String, Object>> danhSachCa = new ArrayList<>();

        for (Map<String, Object> lich : ketQua) {

            Map<String, Object> ca = chuanHoaThongTinCa(lich);

            List<String> gioConTrong = khungGioConTrong(ca);

            ca.put("availableSlots", gioConTrong);
            ca.put("conCho", !gioConTrong.isEmpty());

            // Luôn giữ ca làm việc, kể cả khi đã hết giờ trống.
            danhSachCa.add(ca);
        }

        return danhSachCa;
    }


    // Lấy một ca khám và kiểm tra ca thuộc bác sĩ.
    public Map<String, Object> layCaKham(
            Long bacSiId, Long lichLamViecId) {

        String sql = "SELECT " +
                "llv.id AS scheduleId, " +
                "llv.bac_si_id AS doctorId, " +
                "llv.ngay_lam_viec AS workDate, " +
                "llv.ten_ca AS shiftName, " +
                "llv.gio_bat_dau AS startTime, " +
                "llv.gio_ket_thuc AS endTime, " +
                "llv.so_luong_toida AS maxPatients " +
                "FROM dbo.lich_lam_viec llv " +
                "WHERE llv.id = ? AND llv.bac_si_id = ?";

        Map<String, Object> ca = layMotDong(
                sql,
                "Ca khám không tồn tại hoặc không thuộc bác sĩ đã chọn.",
                lichLamViecId, bacSiId);

        return chuanHoaThongTinCa(ca);
    }

    // Chuẩn hóa dữ liệu cho các trang HTML.
    private Map<String, Object> chuanHoaThongTinCa(
            Map<String, Object> ca) {

        Map<String, Object> ketQua =
                new LinkedHashMap<>(ca);

        ketQua.put("ngay_lam_viec", ca.get("workDate"));
        ketQua.put("ten_ca", ca.get("shiftName"));
        ketQua.put("gio_bat_dau", ca.get("startTime"));
        ketQua.put("gio_ket_thuc", ca.get("endTime"));

        return ketQua;
    }

    // Kiểm tra số lượng và khung giờ còn trống.

    public List<String> khungGioConTrong(
            Map<String, Object> ca) {

        if (ca == null) {
            return List.of();
        }

        LocalDate ngayKham = layNgay(ca.get("workDate"));

        if (ngayKham.isBefore(LocalDate.now())) {
            return List.of();
        }

        Long lichLamViecId = laySoLong(ca.get("scheduleId"));

        int soLuongToiDa = laySoNguyen(ca.get("maxPatients"));

        if (lichLamViecId == null || soLuongToiDa <= 0) {
            return List.of();
        }

        // Kiểm tra tổng số bệnh nhân đã đặt trong cả ca.
        Integer soDaDat = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dbo.lich_hen " +
                        "WHERE lich_lam_viec_id = ? " +
                        "AND trang_thai <> 'DA_HUY'",
                Integer.class,
                lichLamViecId);

        if (soDaDat != null && soDaDat >= soLuongToiDa) {
            return List.of();
        }

        // Lấy các khung giờ đã được đặt.
        List<String> gioDaDat = jdbcTemplate.queryForList(
                "SELECT khung_gio_kham FROM dbo.lich_hen " +
                        "WHERE lich_lam_viec_id = ? " +
                        "AND trang_thai <> 'DA_HUY'",
                String.class,
                lichLamViecId);

        LocalTime batDau = layGio(ca.get("startTime"));
        LocalTime ketThuc = layGio(ca.get("endTime"));

        if (batDau == null || ketThuc == null
                || !batDau.isBefore(ketThuc)) {
            return List.of();
        }

        List<String> ketQua = new ArrayList<>();

        LocalTime hienTai = LocalTime.now();

        // Mỗi bệnh nhân được khám trong 20 phút.
        while (!batDau.plusMinutes(20).isAfter(ketThuc)) {

            LocalTime gioBatDauMoi = batDau;
            LocalTime gioKetThuc = batDau.plusMinutes(20);

            String khungGio =
                    DINH_DANG_GIO.format(gioBatDauMoi)
                            + " - "
                            + DINH_DANG_GIO.format(gioKetThuc);

            boolean chuaQuaGio =
                    !ngayKham.equals(LocalDate.now())
                            || gioBatDauMoi.isAfter(hienTai);

            // Kiểm tra khung giờ mới có bị trùng với lịch đã đặt không.
            boolean khongBiTrung = gioDaDat.stream()
                    .noneMatch(gio -> biTrungKhungGio(
                            gio, gioBatDauMoi, gioKetThuc));

            if (chuaQuaGio && khongBiTrung) {
                ketQua.add(khungGio);
            }

            batDau = gioKetThuc;
        }

        return ketQua;
    }

    // Kiểm tra hai khung giờ có bị chồng lấn không.
    private boolean biTrungKhungGio(
            String khungDaDat,
            LocalTime batDauMoi,
            LocalTime ketThucMoi) {

        if (khungDaDat == null || khungDaDat.isBlank()) {
            return true;
        }

        String[] gio = khungDaDat.trim().split("\\s*-\\s*");

        if (gio.length != 2) {
            return true;
        }

        try {

            LocalTime batDauDaDat =
                    LocalTime.parse(gio[0], DINH_DANG_GIO);

            LocalTime ketThucDaDat =
                    LocalTime.parse(gio[1], DINH_DANG_GIO);

            return batDauMoi.isBefore(ketThucDaDat)
                    && ketThucMoi.isAfter(batDauDaDat);

        } catch (RuntimeException ex) {
            return true;
        }
    }


    // Lưu lịch khám trên Thread riêng.
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long taoLichHen(
            Long benhNhanId, AppointmentForm form) {

        if (benhNhanId == null || form == null
                || form.getDoctorId() == null
                || form.getScheduleId() == null) {

            throw new IllegalArgumentException(
                    "Thông tin đặt lịch không hợp lệ.");
        }

        if (form.getAppointmentDate() == null) {
            throw new IllegalArgumentException(
                    "Ngày khám không hợp lệ.");
        }

        if (form.getTimeSlot() == null
                || form.getTimeSlot().isBlank()) {

            throw new IllegalArgumentException(
                    "Vui lòng chọn khung giờ khám.");
        }

        if (form.getSymptoms() == null
                || form.getSymptoms().isBlank()) {

            throw new IllegalArgumentException(
                    "Vui lòng nhập triệu chứng.");
        }

        if (form.getSymptoms().trim().length() > 2000) {
            throw new IllegalArgumentException(
                    "Triệu chứng không được vượt quá 2.000 ký tự.");
        }

        // Khóa dòng ca làm việc trong thời gian giao dịch.
        String sqlKhoaCa = "SELECT " +
                "llv.id AS scheduleId, " +
                "llv.bac_si_id AS doctorId, " +
                "llv.ngay_lam_viec AS workDate, " +
                "llv.gio_bat_dau AS startTime, " +
                "llv.gio_ket_thuc AS endTime, " +
                "llv.so_luong_toida AS maxPatients " +
                "FROM dbo.lich_lam_viec AS llv WITH (UPDLOCK, HOLDLOCK) " +
                "WHERE llv.id = ? AND llv.bac_si_id = ?";

        Map<String, Object> ca = layMotDong(
                sqlKhoaCa,
                "Ca khám không tồn tại hoặc không thuộc bác sĩ đã chọn.",
                form.getScheduleId(),
                form.getDoctorId());

        LocalDate ngayTrongCa =
                layNgay(ca.get("workDate"));

        if (!form.getAppointmentDate().equals(ngayTrongCa)) {
            throw new IllegalArgumentException(
                    "Ngày khám không khớp với ca làm việc.");
        }

        if (ngayTrongCa.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Không thể đặt lịch trong quá khứ.");
        }

        if (!khungGioConTrong(ca).contains(form.getTimeSlot())) {
            throw new IllegalArgumentException(
                    "Khung giờ vừa chọn không còn trống. Vui lòng chọn giờ khác.");
        }

        // Lưu đủ ba khóa ngoại theo đúng createdb.sql.
        String sqlThem = "INSERT INTO dbo.lich_hen " +
                "(benh_nhan_id, bac_si_id, lich_lam_viec_id, " +
                "ngay_kham, khung_gio_kham, trieu_chung_benh, " +
                "trang_thai, ngay_tao) " +
                "OUTPUT INSERTED.id " +
                "VALUES (?, ?, ?, ?, ?, ?, 'CHO_XAC_NHAN', GETDATE())";

        return jdbcTemplate.queryForObject(
                sqlThem,
                Long.class,
                benhNhanId,
                form.getDoctorId(),
                form.getScheduleId(),
                Date.valueOf(ngayTrongCa),
                form.getTimeSlot(),
                form.getSymptoms().trim());
    }

    // Lấy thông tin lịch hẹn đã được lưu trong database.
    public Map<String, Object> layThongTinLichHen(
            Long lichHenId, Long benhNhanId) {

        String sql = "SELECT " +
                "lh.id AS appointmentId, " +
                "lh.ngay_kham AS appointmentDate, " +
                "lh.khung_gio_kham AS timeSlot, " +
                "lh.trieu_chung_benh AS symptoms, " +
                "lh.trang_thai AS status, " +
                "bn.ho_ten AS patientName, " +
                "tk.so_dien_thoai AS phone, " +
                "b.ho_ten AS tenBacSi, " +
                "b.hoc_ham_hoc_vi AS hocVi, " +
                "ck.ten_chuyen_khoa AS specialty, " +
                "pk.ten_phong AS tenPhong, " +
                "pk.khu_vuc AS khuVuc " +
                "FROM dbo.lich_hen lh " +
                "INNER JOIN dbo.benh_nhan bn ON bn.id = lh.benh_nhan_id " +
                "INNER JOIN dbo.tai_khoan tk ON tk.id = bn.tai_khoan_id " +
                "INNER JOIN dbo.bac_si b ON b.id = lh.bac_si_id " +
                "INNER JOIN dbo.lich_lam_viec llv " +
                "ON llv.id = lh.lich_lam_viec_id " +
                "AND llv.bac_si_id = lh.bac_si_id " +
                "LEFT JOIN dbo.chuyen_khoa ck ON ck.id = b.chuyen_khoa_id " +
                "LEFT JOIN dbo.phong_kham pk ON pk.id = llv.phong_kham_id " +
                "WHERE lh.id = ? AND lh.benh_nhan_id = ?";

        Map<String, Object> lichHen = layMotDong(
                sql,
                "Không tìm thấy lịch hẹn của bệnh nhân này.",
                lichHenId, benhNhanId);

        String hocVi =
                Objects.toString(lichHen.get("hocVi"), "");

        String tenBacSi =
                Objects.toString(lichHen.get("tenBacSi"), "");

        lichHen.put(
                "doctorName",
                (hocVi + " " + tenBacSi).trim());

        if (lichHen.get("specialty") == null) {
            lichHen.put("specialty", "Chưa cập nhật chuyên khoa");
        }

        return lichHen;
    }

    private Map<String, Object> layMotDong(
            String sql,
            String thongBaoLoi,
            Object... thamSo) {

        try {
            return jdbcTemplate.queryForMap(sql, thamSo);
        } catch (EmptyResultDataAccessException ex) {
            throw new IllegalArgumentException(thongBaoLoi);
        }
    }

    private LocalDate layNgay(Object giaTri) {

        if (giaTri instanceof Date ngay) {
            return ngay.toLocalDate();
        }

        if (giaTri instanceof LocalDate ngay) {
            return ngay;
        }

        throw new IllegalArgumentException(
                "Ngày làm việc trong database không hợp lệ.");
    }

    private LocalTime layGio(Object giaTri) {

        if (giaTri instanceof Time gio) {
            return gio.toLocalTime();
        }

        if (giaTri instanceof LocalTime gio) {
            return gio;
        }

        return null;
    }

    private Long laySoLong(Object giaTri) {
        return giaTri instanceof Number so
                ? so.longValue() : null;
    }

    private int laySoNguyen(Object giaTri) {
        return giaTri instanceof Number so
                ? so.intValue() : 0;
    }
}
