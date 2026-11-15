package com.example.baitapluyen.controller;

import com.example.baitapluyen.dto.AppointmentForm;
import com.example.baitapluyen.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Map;

@Controller
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // 1. Chi tiết bác sĩ
    @GetMapping("/ho-so-bac-si/{id}")
    public String doctorDetail(@PathVariable Long id, Model model) {
        Map<String, Object> doctor = patientService.getDoctor(id);
        if (doctor == null) return "redirect:/";

        model.addAttribute("doctor", doctor);
        model.addAttribute("schedules", patientService.getSchedules(id));
        return "BenhNhan/ChiTietBacSi";
    }

    // 2. Hiển thị trang đặt lịch
    @GetMapping("/dat-lich-kham")
    public String bookingPage(@RequestParam Long bacSiId,
                              @RequestParam Long lichLamViecId,
                              HttpSession session,
                              Model model) {
        Long benhNhanId = (Long) session.getAttribute("benhNhanId");
        if (benhNhanId == null) return "redirect:/dang-nhap";

        Map<String, Object> doctor = patientService.getDoctor(bacSiId);
        Map<String, Object> patient = patientService.getPatientById(benhNhanId);
        Map<String, Object> schedule = patientService.getSchedule(lichLamViecId);

        if (doctor == null || patient == null || schedule == null
                || !patientService.isScheduleOfDoctor(lichLamViecId, bacSiId)) {
            return "redirect:/";
        }

        AppointmentForm form = new AppointmentForm();
        form.setDoctorId(bacSiId);
        form.setScheduleId(lichLamViecId);
        form.setAppointmentDate(((Date) schedule.get("ngay_lam_viec")).toLocalDate());

        model.addAttribute("schedule", schedule);
        model.addAttribute("doctor", doctor);
        model.addAttribute("patient", patient);
        model.addAttribute("form", form);
        model.addAttribute("availableSlots", patientService.getAvailableSlots(lichLamViecId));
        return "BenhNhan/DatLichKham";
    }

    // Xử lý gửi biểu mẫu đặt lịch
    @PostMapping("/dat-lich-kham")
    public String createBooking(@ModelAttribute("form") AppointmentForm form,
                                HttpSession session,
                                Model model) {
        Long benhNhanId = (Long) session.getAttribute("benhNhanId");
        if (benhNhanId == null) return "redirect:/dang-nhap";

        try {
            Long appointmentId = patientService.createAppointment(benhNhanId, form);
            return "redirect:/dat-lich-kham/thanh-cong/" + appointmentId;
        } catch (IllegalArgumentException e) {
            Map<String, Object> doctor = patientService.getDoctor(form.getDoctorId());
            Map<String, Object> patient = patientService.getPatientById(benhNhanId);
            Map<String, Object> schedule = patientService.getSchedule(form.getScheduleId());

            model.addAttribute("schedule", schedule);
            model.addAttribute("doctor", doctor);
            model.addAttribute("patient", patient);
            model.addAttribute("availableSlots",
                    patientService.getAvailableSlots(form.getScheduleId()));
            model.addAttribute("error", e.getMessage());
            model.addAttribute("form", form);
            return "BenhNhan/DatLichKham";
        }
    }

    // 3. Trang đặt lịch thành công
    @GetMapping("/dat-lich-kham/thanh-cong/{id}")
    public String bookingSuccess(@PathVariable Long id,
                                 HttpSession session,
                                 Model model) {
        Long benhNhanId = (Long) session.getAttribute("benhNhanId");
        if (benhNhanId == null) return "redirect:/dang-nhap";

        Map<String, Object> appointment = patientService.getAppointment(id, benhNhanId);
        if (appointment == null) return "redirect:/";

        model.addAttribute("appointment", appointment);
        return "BenhNhan/DatLichKhamThanhCong";
    }
}
