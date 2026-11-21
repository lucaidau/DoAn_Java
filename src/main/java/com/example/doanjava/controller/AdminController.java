package com.example.doanjava.controller;

import com.example.doanjava.dto.admin.BacSiDTO;
import com.example.doanjava.dto.admin.SuaBacSiDTO;
import com.example.doanjava.dto.admin.TaiKhoanDTO;
import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.LichHen;
import com.example.doanjava.service.BacSiService;
import com.example.doanjava.service.ChuyenKhoaService;
import com.example.doanjava.service.LichHenService;
import com.example.doanjava.service.admin.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final TaiKhoanService taiKhoanService;
    private final ChuyenKhoaService chuyenKhoaService;
    private final BacSiService bacSiService;


    public AdminController(TaiKhoanService tk, ChuyenKhoaService ck, BacSiService bs)
    {
        this.taiKhoanService = tk;
        this.chuyenKhoaService =ck;
        this.bacSiService = bs;
    }

    @GetMapping("/taiKhoan")
    public String QLTaiKhoan(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String vaiTro,
            @RequestParam(required = false) Boolean trangThai,
            Model model)
    {
        Page<TaiKhoanDTO> taiKhoanDTOPage = taiKhoanService.getTaiKhoan(page, size, keyword, vaiTro, trangThai);
        model.addAttribute("taiKhoan", taiKhoanDTOPage.getContent());
        model.addAttribute("trangHienTai", page);
        model.addAttribute("tongTrang", taiKhoanDTOPage.getTotalPages());
        model.addAttribute("tongTaiKhoan", taiKhoanDTOPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("vaiTro", vaiTro);
        model.addAttribute("trangThai", trangThai);
        return "/admin/QLTaiKhoan";
    }

    @PostMapping("/taiKhoan/doiTrangThai/{id}")
    public String doiTrangThaiTaiKhoan(@PathVariable Long id, RedirectAttributes redirectAttributes)
    {
        try {
            boolean dangHoatDong = taiKhoanService.doiTrangThaiTaiKhoan(id);
            String thongBao = dangHoatDong ? "Đã mở khóa tài khoản thành công!" : "Đã khóa tài khoản thành công!";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", thongBao);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/taiKhoan";
    }

    @Autowired
    private LichHenService lichHenService;

    @GetMapping("/dashboard")
    public String QLLichHen(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "ALL") String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            Model model
    )
    {
        Page<LichHen> pageLichHen = lichHenService.danhSach(search, trangThai, from, to, page);

        model.addAttribute("pageLichHen", pageLichHen);
        model.addAttribute("dsLichHen", pageLichHen.getContent());
        model.addAttribute("trangHienTai", page);
        model.addAttribute("tongTrang", pageLichHen.getTotalPages());

        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        model.addAttribute("tongHomNay", lichHenService.tongHomNay());
        model.addAttribute("choXacNhan", lichHenService.thongKe("CHO_XAC_NHAN"));
        model.addAttribute("daXacNhan", lichHenService.thongKe("DA_XAC_NHAN"));
        model.addAttribute("daKham", lichHenService.thongKe("DA_KHAM"));
        model.addAttribute("daHuy", lichHenService.thongKe("DA_HUY"));
        return "/admin/QLLichHen";
    }

    @GetMapping("/dashboard/chiTiet/{id}")
    public String chiTietLichHen(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            LichHen lich = lichHenService.chiTiet(id);
            model.addAttribute("lichHen", lich);
            model.addAttribute("sinhHieu", lichHenService.sinhHieu(id).orElse(null));
            lichHenService.hoSo(id).ifPresent(hoSo -> {
                model.addAttribute("hoSoBenhAn", hoSo);
                model.addAttribute("dsDonThuoc", lichHenService.donThuoc(hoSo.getId()));
            });

            return "/admin/ChiTietLichHen";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @GetMapping("/bacSi")
    public String QLBacSi(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String chuyenKhoa,
            @RequestParam(required = false) String hocVi,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    )
    {
        Page<BacSi> pageBacSi = bacSiService.LayDSHoacTimKiem(search, chuyenKhoa, hocVi, trangThai, page, size);
        model.addAttribute("dsBacSi", pageBacSi.getContent());
        model.addAttribute("phongKhamMap", bacSiService.layPhongKhamTheoChuyenKhoa(pageBacSi.getContent()));
        model.addAttribute("pageBacSi", pageBacSi);
        model.addAttribute("trangHienTai", pageBacSi.getNumber());
        model.addAttribute("tongTrang", pageBacSi.getTotalPages());
        model.addAttribute("tongBacSi", pageBacSi.getTotalElements());
        model.addAttribute("size", size);

        model.addAttribute("dsChuyenKhoa", bacSiService.layDanhSachChuyenKhoa());
        model.addAttribute("dsHocVi", bacSiService.layDanhSachHocVi());

        model.addAttribute("search", search);
        model.addAttribute("chuyenKhoa", chuyenKhoa);
        model.addAttribute("hocVi", hocVi);
        model.addAttribute("trangThai", trangThai);
        return  "/admin/QLBacSi";
    }

    @PostMapping("/bacSi/them")
    public String ThemBacSi(@ModelAttribute BacSiDTO dto, RedirectAttributes redirectAttributes)
    {
        try {
            bacSiService.Them(dto);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Thêm bác sĩ mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi khi thêm bác sĩ: " + e.getMessage());
        }
        return "redirect:/admin/bacSi";
    }

    @GetMapping("/bacSi/sua/{id}")
    public String FormSuaBacSi(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes)
    {
        try {
            model.addAttribute("bacSiDTO", bacSiService.layThongTinSua(id));
            model.addAttribute("dsChuyenKhoa", bacSiService.layDanhSachChuyenKhoa());
            return "/admin/FormSuaBacSi";
        } catch (Exception e)
        {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/bacSi";
        }
    }

    @PostMapping("/bacSi/sua")
    public String SuaBacSi(@ModelAttribute SuaBacSiDTO dto, RedirectAttributes redirectAttributes)
    {
        try {
            bacSiService.capNhatBacSi(dto);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cập nhật thông tin bác sĩ thành công");
        } catch (Exception e)
        {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());

        }
        return "redirect:/admin/bacSi";
    }

    @GetMapping("/chuyenKhoa")
    public String QLChuyenKhoa(Model model)
    {
        model.addAttribute("dsChuyenKhoa", chuyenKhoaService.layDanhSachChuyenKhoa());
        return "/admin/QLChuyenKhoa";
    }

}
