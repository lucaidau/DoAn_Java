package com.example.doanjava.controller;

import com.example.doanjava.dto.admin.TaiKhoanDTO;
import com.example.doanjava.entity.LichHen;
import com.example.doanjava.service.LichHenService;
import com.example.doanjava.service.admin.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TaiKhoanService taiKhoanService;



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

        model.addAttribute("dsLichHen", pageLichHen.getContent());
        model.addAttribute("trangHienTai", page);
        model.addAttribute("tongTrang", pageLichHen.getTotalPages());

        model.addAttribute("timKiem", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("tuNgay", from);
        model.addAttribute("denNgay", to);

        model.addAttribute("tongHomNay", lichHenService.tongHomNay());
        model.addAttribute("choXacNhan", lichHenService.thongKe("CHO_XAC_NHAN"));
        model.addAttribute("daXacNhan", lichHenService.thongKe("DA_XAC_NHAN"));
        model.addAttribute("daKham", lichHenService.thongKe("DA_KHAM"));
        model.addAttribute("daHuy", lichHenService.thongKe("DA_HUY"));
        return "/admin/QLLichHen";
    }

    @GetMapping("/bacSi")
    public String QLBacSi()
    {
        return  "/admin/QLBacSi";
    }

    @GetMapping("/chuyenKhoa")
    public String QLChuyenKhoa()
    {
        return "/admin/QLChuyenKhoa";
    }

}
