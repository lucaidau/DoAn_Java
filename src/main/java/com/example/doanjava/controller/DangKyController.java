package vn.carehealth.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.carehealth.dto.ThongTinDangKy;
import vn.carehealth.service.DangKyService;

@Controller
public class DangKyController {
    private final DangKyService dangKyService;

    public DangKyController(DangKyService dangKyService) {
        this.dangKyService = dangKyService;
    }

    @GetMapping("/register")
    public String hienThiDangKy() {
        return "DangKi";
    }

    @PostMapping("/register")
    public String xuLyDangKy(
            @Valid @ModelAttribute ThongTinDangKy thongTin,
            BindingResult ketQua,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (ketQua.hasErrors()) {
            model.addAttribute("registerError", ketQua.getAllErrors().get(0).getDefaultMessage());
            return "DangKi";
        }
        try {
            dangKyService.dangKy(thongTin);
            redirectAttributes.addAttribute("registered", "");
            return "redirect:/login";
        } catch (IllegalArgumentException | DataAccessException ex) {
            model.addAttribute("registerError", ex instanceof DataAccessException
                    ? "Không thể lưu dữ liệu. Kiểm tra kết nối SQL Server."
                    : ex.getMessage());
            return "DangKi";
        }
    }
}
