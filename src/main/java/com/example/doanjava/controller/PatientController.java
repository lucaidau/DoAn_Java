
package com.example.doanjava.controller;

import com.example.doanjava.dto.AppointmentForm;
import com.example.doanjava.service.DatLichKhamService;
import com.example.doanjava.service.XuLyDatLichKham;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class PatientController {

    private final DatLichKhamService datLichKhamService;

    public PatientController(DatLichKhamService datLichKhamService) {
        this.datLichKhamService = datLichKhamService;
    }

    // Nghiệp vụ 1: Xem chi tiết hồ sơ bác sĩ.
    @GetMapping("/ho-so-bac-si/{id}")
    public String chiTietBacSi(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        kiemTraQuyenBenhNhan(authentication);

        Map<String, Object> benhNhan =
                datLichKhamService.layBenhNhanDangNhap(
                        authentication.getName());

        Map<String, Object> bacSi =
                datLichKhamService.layThongTinBacSi(id);

        List<Map<String, Object>> danhSachCa =
                datLichKhamService.layLichSapToi(id);


        Map<String, Object> caCoGioTrong = danhSachCa.stream()
                .filter(ca -> Boolean.TRUE.equals(ca.get("conCho")))
                .findFirst()
                .orElse(null);

        Map<String, Object> caDauTien = caCoGioTrong != null
                ? caCoGioTrong
                : danhSachCa.isEmpty()
                  ? null
                  : danhSachCa.get(0);

        List<String> gioConTrong = caCoGioTrong == null
                ? List.of()
                : datLichKhamService.khungGioConTrong(caCoGioTrong);

        boolean coGioTrong = caCoGioTrong != null;


        model.addAttribute("doctor", bacSi);
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("schedules", danhSachCa);
        model.addAttribute("firstSchedule", caDauTien);
        model.addAttribute("firstSlots", gioConTrong);
        model.addAttribute("hasAvailableSlots", coGioTrong);
        model.addAttribute("benhNhan", benhNhan);

        return "BenhNhan/ChiTietBacSi";
    }

    // Nghiệp vụ 2: Hiển thị form đặt lịch khám.
    @GetMapping("/dat-lich-kham")
    public String hienThiDatLich(
            @RequestParam Long bacSiId,
            @RequestParam Long lichLamViecId,
            @RequestParam(required = false) String khungGio,
            Authentication authentication,
            Model model) {

        kiemTraQuyenBenhNhan(authentication);

        Map<String, Object> benhNhan =
                datLichKhamService.layBenhNhanDangNhap(
                        authentication.getName());

        Map<String, Object> bacSi =
                datLichKhamService.layThongTinBacSi(bacSiId);

        Map<String, Object> caKham =
                datLichKhamService.layCaKham(
                        bacSiId, lichLamViecId);

        AppointmentForm form = new AppointmentForm();

        form.setDoctorId(bacSiId);
        form.setScheduleId(lichLamViecId);

        form.setAppointmentDate(
                ((java.sql.Date) caKham.get("workDate")).toLocalDate());

        List<String> gioConTrong =
                datLichKhamService.khungGioConTrong(caKham);

        if (khungGio != null && gioConTrong.contains(khungGio)) {
            form.setTimeSlot(khungGio);
        }

        napDuLieuForm(
                model, bacSi, caKham,
                benhNhan, form, null, gioConTrong);

        return "BenhNhan/DatLichKham";
    }

    // Nghiệp vụ 2: Nhận form và chạy công việc trên Thread riêng.
    @PostMapping("/dat-lich-kham")
    public String datLich(
            @ModelAttribute("form") AppointmentForm form,
            Authentication authentication,
            Model model) {

        kiemTraQuyenBenhNhan(authentication);

        Map<String, Object> benhNhan =
                datLichKhamService.layBenhNhanDangNhap(
                        authentication.getName());

        if (form.getDoctorId() == null
                || form.getScheduleId() == null) {
            return "redirect:/patient/doctors";
        }

        Long benhNhanId =
                ((Number) benhNhan.get("id")).longValue();

        XuLyDatLichKham congViec = new XuLyDatLichKham(
                datLichKhamService,
                benhNhanId,
                form);

        Thread luongDatLich = new Thread(
                congViec,
                "luong-dat-lich-" + benhNhanId);

        luongDatLich.start();

        doiLuongHoanTat(luongDatLich);

        if (congViec.getMaLichHen() != null) {
            return "redirect:/dat-lich-kham/thanh-cong/"
                    + congViec.getMaLichHen();
        }

        String thongBaoLoi =
                congViec.getLoi() instanceof IllegalArgumentException
                        ? congViec.getLoi().getMessage()
                        : "Hệ thống chưa thể lưu lịch hẹn. Vui lòng thử lại.";

        try {

            Map<String, Object> bacSi =
                    datLichKhamService.layThongTinBacSi(
                            form.getDoctorId());

            Map<String, Object> caKham =
                    datLichKhamService.layCaKham(
                            form.getDoctorId(),
                            form.getScheduleId());

            List<String> gioConTrong =
                    datLichKhamService.khungGioConTrong(caKham);

            napDuLieuForm(
                    model,
                    bacSi,
                    caKham,
                    benhNhan,
                    form,
                    thongBaoLoi,
                    gioConTrong);

            return "BenhNhan/DatLichKham";

        } catch (IllegalArgumentException ex) {
            return "redirect:/patient/doctors";
        }
    }

    // Nghiệp vụ 3: Hiển thị kết quả đặt lịch thành công.
    @GetMapping("/dat-lich-kham/thanh-cong/{id}")
    public String datLichThanhCong(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        kiemTraQuyenBenhNhan(authentication);

        Map<String, Object> benhNhan =
                datLichKhamService.layBenhNhanDangNhap(
                        authentication.getName());

        Long benhNhanId =
                ((Number) benhNhan.get("id")).longValue();

        model.addAttribute(
                "appointment",
                datLichKhamService.layThongTinLichHen(
                        id, benhNhanId));

        model.addAttribute("benhNhan", benhNhan);

        return "BenhNhan/DatLichKhamThanhCong";
    }

    // Nạp dữ liệu cho form đặt lịch.
    private void napDuLieuForm(
            Model model,
            Map<String, Object> bacSi,
            Map<String, Object> caKham,
            Map<String, Object> benhNhan,
            AppointmentForm form,
            String loi,
            List<String> gioConTrong) {

        model.addAttribute("doctor", bacSi);
        model.addAttribute("schedule", caKham);
        model.addAttribute("patient", benhNhan);
        model.addAttribute("form", form);
        model.addAttribute("availableSlots", gioConTrong);

        if (loi != null && !loi.isBlank()) {
            model.addAttribute("error", loi);
        }
    }

    // Chỉ cho phép tài khoản bệnh nhân sử dụng ba nghiệp vụ.
    private void kiemTraQuyenBenhNhan(
            Authentication authentication) {

        boolean dungQuyen = authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(quyen ->
                        "ROLE_BENH_NHAN".equals(quyen.getAuthority()));

        if (!dungQuyen) {
            throw new AccessDeniedException(
                    "Bạn không có quyền truy cập chức năng dành cho bệnh nhân.");
        }
    }

    // Chờ Thread hoàn thành để lấy mã lịch hẹn.
    private void doiLuongHoanTat(Thread luong) {

        boolean biNgat = false;

        while (luong.isAlive()) {
            try {
                luong.join();
            } catch (InterruptedException ex) {
                biNgat = true;
            }
        }

        if (biNgat) {
            Thread.currentThread().interrupt();
        }
    }
}
