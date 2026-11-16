package com.example.doanjava.controller;

import com.example.doanjava.dto.AppointmentForm;
import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.service.PatientBookingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PatientController {
    private final PatientBookingService patientBookingService;

    public PatientController(PatientBookingService patientBookingService) {
        this.patientBookingService = patientBookingService;
    }

    @GetMapping("/patient/dashboard")
    public String dashboard() {
        return "redirect:/patient/doctors";
    }

    @GetMapping("/patient/booking")
    public String bookingShortcut() {
        return "redirect:/patient/doctors";
    }

    @GetMapping("/patient/specialties")
    public String specialtiesShortcut() {
        return "redirect:/patient/doctors";
    }

    @GetMapping("/patient/doctors")
    public String danhSachBacSi(
            @RequestParam(defaultValue = "") String keyword,
            Authentication authentication,
            Model model) {
        BenhNhan benhNhan = patientBookingService.layBenhNhanDangNhap(authentication);
        model.addAttribute("benhNhan", benhNhan);
        model.addAttribute("bacSis", patientBookingService.timBacSi(keyword));
        model.addAttribute("keyword", keyword);
        return "BenhNhan/DanhSachBacsi";
    }

    @GetMapping("/ho-so-bac-si/{id}")
    public String chiTietBacSi(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {
        BacSi bacSi = patientBookingService.layBacSi(id);
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("doctor", patientBookingService.thongTinBacSi(bacSi));
        var schedules = patientBookingService.lichSapToi(bacSi.getId());
        model.addAttribute("schedules", schedules);
        model.addAttribute("firstSchedule", schedules.isEmpty() ? null : schedules.get(0));
        model.addAttribute("firstSlots", schedules.isEmpty()
                ? java.util.List.of()
                : patientBookingService.khungGioConTrong(
                        patientBookingService.layCaKham(bacSi.getId(), (Long) schedules.get(0).get("scheduleId"))));
        model.addAttribute("benhNhan", patientBookingService.layBenhNhanDangNhap(authentication));
        return "BenhNhan/ChiTietBacSi";
    }

    @GetMapping("/dat-lich-kham")
    public String hienThiDatLich(
            @RequestParam Long bacSiId,
            @RequestParam Long lichLamViecId,
            @RequestParam(required = false) String khungGio,
            Authentication authentication,
            Model model) {
        BenhNhan benhNhan = patientBookingService.layBenhNhanDangNhap(authentication);
        BacSi bacSi = patientBookingService.layBacSi(bacSiId);
        var lichLamViec = patientBookingService.layCaKham(bacSiId, lichLamViecId);
        var form = new AppointmentForm();
        form.setDoctorId(bacSiId);
        form.setScheduleId(lichLamViecId);
        form.setAppointmentDate(lichLamViec.getNgayLamViec());
        var availableSlots = patientBookingService.khungGioConTrong(lichLamViec);
        if (khungGio != null && availableSlots.contains(khungGio)) {
            form.setTimeSlot(khungGio);
        }

        model.addAttribute("doctor", patientBookingService.thongTinBacSi(bacSi));
        model.addAttribute("schedule", patientBookingService.thongTinCaKham(lichLamViec));
        model.addAttribute("patient", patientBookingService.thongTinBenhNhan(benhNhan));
        model.addAttribute("form", form);
        model.addAttribute("availableSlots", availableSlots);
        return "BenhNhan/DatLichKham";
    }

    @PostMapping("/dat-lich-kham")
    public String taoDatLich(
            @ModelAttribute("form") AppointmentForm form,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        BenhNhan benhNhan = patientBookingService.layBenhNhanDangNhap(authentication);
        try {
            Long appointmentId = patientBookingService.taoLichHen(benhNhan, form);
            return "redirect:/dat-lich-kham/thanh-cong/" + appointmentId;
        } catch (IllegalArgumentException ex) {
            if (form.getDoctorId() == null || form.getScheduleId() == null) {
                redirectAttributes.addFlashAttribute("loi", ex.getMessage());
                return "redirect:/patient/doctors";
            }
            try {
                BacSi bacSi = patientBookingService.layBacSi(form.getDoctorId());
                var lichLamViec = patientBookingService.layCaKham(form.getDoctorId(), form.getScheduleId());
                model.addAttribute("doctor", patientBookingService.thongTinBacSi(bacSi));
                model.addAttribute("schedule", patientBookingService.thongTinCaKham(lichLamViec));
                model.addAttribute("patient", patientBookingService.thongTinBenhNhan(benhNhan));
                model.addAttribute("availableSlots", patientBookingService.khungGioConTrong(lichLamViec));
                model.addAttribute("error", ex.getMessage());
                model.addAttribute("form", form);
                return "BenhNhan/DatLichKham";
            } catch (IllegalArgumentException invalidSchedule) {
                redirectAttributes.addFlashAttribute("loi", invalidSchedule.getMessage());
                return "redirect:/patient/doctors";
            }
        }
    }

    @GetMapping("/dat-lich-kham/thanh-cong/{id}")
    public String datLichThanhCong(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {
        BenhNhan benhNhan = patientBookingService.layBenhNhanDangNhap(authentication);
        model.addAttribute("appointment", patientBookingService.thongTinLichHen(id, benhNhan.getId()));
        model.addAttribute("benhNhan", benhNhan);
        return "BenhNhan/DatLichKhamThanhCong";
    }

    @GetMapping("/patient/appointments")
    public String lichHenCuaToi(Authentication authentication, Model model) {
        BenhNhan benhNhan = patientBookingService.layBenhNhanDangNhap(authentication);
        model.addAttribute("benhNhan", benhNhan);
        model.addAttribute("appointments", patientBookingService.lichHenCuaBenhNhan(benhNhan.getId()));
        return "BenhNhan/LichHenCuaToi";
    }

    @PostMapping("/patient/appointments/{id}/cancel")
    public String huyLichHen(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        BenhNhan benhNhan = patientBookingService.layBenhNhanDangNhap(authentication);
        try {
            patientBookingService.huyLichHen(id, benhNhan.getId());
            redirectAttributes.addFlashAttribute("thanhCong", "Đã gửi yêu cầu hủy lịch khám.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/patient/appointments";
    }
}
