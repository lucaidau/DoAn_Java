package com.example.baitapluyen.service;

import com.example.baitapluyen.dto.AppointmentForm;
import com.example.baitapluyen.model.*;
import com.example.baitapluyen.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class PatientService {
    private final BacSiRepository bacSiRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final LichLamViecRepository lichLamViecRepository;
    private final LichHenRepository lichHenRepository;

    public PatientService(BacSiRepository bacSiRepository,
                          BenhNhanRepository benhNhanRepository,
                          LichLamViecRepository lichLamViecRepository,
                          LichHenRepository lichHenRepository) {
        this.bacSiRepository = bacSiRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.lichLamViecRepository = lichLamViecRepository;
        this.lichHenRepository = lichHenRepository;
    }

    // Chức năng 1: Lấy chi tiết bác sĩ và danh sách ca làm việc.
    @Transactional(readOnly = true)
    public Map<String, Object> getDoctor(Long doctorId) {
        BacSi bs = bacSiRepository.findById(doctorId).orElse(null);
        if (bs == null) return null;

        Map<String, Object> doctor = new HashMap<>();
        doctor.put("doctorId", bs.getId());
        doctor.put("doctorName", bs.getHoTen());
        doctor.put("degree", bs.getHocHamHocVi());
        doctor.put("experience", bs.getSoNamKinhNghiem());
        doctor.put("introduction", bs.getGioiThieu());
        doctor.put("specialty", bs.getChuyenKhoa().getTenChuyenKhoa());
        return doctor;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSchedules(Long doctorId) {
        List<LichLamViec> schedules =
                lichLamViecRepository
                        .findByBacSi_IdAndNgayLamViecGreaterThanEqualOrderByNgayLamViecAscGioBatDauAsc(
                                doctorId, LocalDate.now());

        List<Map<String, Object>> result = new ArrayList<>();
        for (LichLamViec item : schedules) {
            Map<String, Object> row = new HashMap<>();
            row.put("scheduleId", item.getId());
            row.put("workDate", Date.valueOf(item.getNgayLamViec()));
            row.put("shiftName", item.getTenCa());
            row.put("startTime", Time.valueOf(item.getGioBatDau()));
            row.put("endTime", Time.valueOf(item.getGioKetThuc()));
            row.put("maxPatients", item.getSoLuongToiDa());
            result.add(row);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPatientById(Long patientId) {
        BenhNhan bn = benhNhanRepository.findById(patientId).orElse(null);
        if (bn == null) return null;

        Map<String, Object> patient = new HashMap<>();
        patient.put("patientId", bn.getId());
        patient.put("fullName", bn.getHoTen());
        patient.put("birthDate", bn.getNgaySinh() == null ? null : Date.valueOf(bn.getNgaySinh()));
        patient.put("gender", bn.getGioiTinh());
        patient.put("phone", bn.getTaiKhoan().getSoDienThoai());
        return patient;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSchedule(Long scheduleId) {
        LichLamViec item = lichLamViecRepository.findById(scheduleId).orElse(null);
        if (item == null) return null;

        Map<String, Object> schedule = new HashMap<>();
        schedule.put("id", item.getId());
        schedule.put("bac_si_id", item.getBacSi().getId());
        schedule.put("ngay_lam_viec", Date.valueOf(item.getNgayLamViec()));
        schedule.put("gio_bat_dau", Time.valueOf(item.getGioBatDau()));
        schedule.put("gio_ket_thuc", Time.valueOf(item.getGioKetThuc()));
        schedule.put("so_luong_toida", item.getSoLuongToiDa());
        schedule.put("ten_ca", item.getTenCa());
        return schedule;
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableSlots(Long scheduleId) {
        LichLamViec schedule = lichLamViecRepository.findById(scheduleId).orElse(null);
        if (schedule == null || schedule.getNgayLamViec().isBefore(LocalDate.now())) {
            return Collections.emptyList();
        }

        List<LichHen> bookings =
                lichHenRepository.findByLichLamViec_IdAndTrangThaiNot(scheduleId, "DA_HUY");
        Set<String> bookedSlots = new HashSet<>();
        for (LichHen booking : bookings) bookedSlots.add(booking.getKhungGioKham());

        List<String> available = new ArrayList<>();
        LocalTime start = schedule.getGioBatDau();
        LocalTime end = schedule.getGioKetThuc();
        LocalTime now = LocalTime.now();

        while (!start.plusMinutes(30).isAfter(end)) {
            LocalTime slotEnd = start.plusMinutes(30);
            String slot = String.format("%s - %s", start, slotEnd);
            if (!bookedSlots.contains(slot)
                    && (!schedule.getNgayLamViec().equals(LocalDate.now()) || start.isAfter(now))) {
                available.add(slot);
            }
            start = slotEnd;
        }
        return available;
    }

    // Chức năng 2: Kiểm tra dữ liệu và lưu lịch hẹn.
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long createAppointment(Long patientId, AppointmentForm form) {
        if (patientId == null || form.getDoctorId() == null || form.getScheduleId() == null
                || form.getAppointmentDate() == null || form.getTimeSlot() == null
                || form.getTimeSlot().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin đặt lịch.");
        }

        BenhNhan patient = benhNhanRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bệnh nhân."));
        BacSi doctor = bacSiRepository.findById(form.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ."));
        LichLamViec schedule = lichLamViecRepository.findById(form.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Ca làm việc không tồn tại."));

        if (!schedule.getBacSi().getId().equals(doctor.getId())
                || !schedule.getNgayLamViec().equals(form.getAppointmentDate())) {
            throw new IllegalArgumentException("Thông tin ca khám không hợp lệ.");
        }
        if (!getAvailableSlots(schedule.getId()).contains(form.getTimeSlot())) {
            throw new IllegalArgumentException("Khung giờ này không còn trống.");
        }
        long bookedCount = lichHenRepository
                .countByLichLamViec_IdAndTrangThaiNot(schedule.getId(), "DA_HUY");
        if (bookedCount >= schedule.getSoLuongToiDa()) {
            throw new IllegalArgumentException("Ca khám đã hết số lượng tiếp nhận.");
        }

        LichHen appointment = new LichHen();
        appointment.setBenhNhan(patient);
        appointment.setBacSi(doctor);
        appointment.setLichLamViec(schedule);
        appointment.setNgayKham(form.getAppointmentDate());
        appointment.setKhungGioKham(form.getTimeSlot());
        appointment.setTrieuChungBenh(form.getSymptoms());
        appointment.setTrangThai("CHO_XAC_NHAN");

        return lichHenRepository.save(appointment).getId();
    }

    public boolean isScheduleOfDoctor(Long scheduleId, Long doctorId) {
        LichLamViec schedule = lichLamViecRepository.findById(scheduleId).orElse(null);
        return schedule != null && schedule.getBacSi().getId().equals(doctorId);
    }

    // Chức năng 3: Lấy lịch hẹn vừa tạo, đồng thời kiểm tra chủ sở hữu.
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(Long appointmentId, Long patientId) {
        LichHen lh = lichHenRepository.findByIdAndBenhNhan_Id(appointmentId, patientId)
                .orElse(null);
        if (lh == null) return null;

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("appointmentId", lh.getId());
        appointment.put("patientName", lh.getBenhNhan().getHoTen());
        appointment.put("phone", lh.getBenhNhan().getTaiKhoan().getSoDienThoai());
        appointment.put("doctorName", lh.getBacSi().getHoTen());
        appointment.put("specialty", lh.getBacSi().getChuyenKhoa().getTenChuyenKhoa());
        appointment.put("appointmentDate", Date.valueOf(lh.getNgayKham()));
        appointment.put("timeSlot", lh.getKhungGioKham());
        appointment.put("symptoms", lh.getTrieuChungBenh());
        appointment.put("status", lh.getTrangThai());
        return appointment;
    }
}
