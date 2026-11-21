package com.example.doanjava.controller;

import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.service.HoSoService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {
        HomeController.class,
        BacSiController.class,
        ChuyenKhoaController.class
})
public class PatientHeaderModelAdvice {
    private final HoSoService hoSoService;

    public PatientHeaderModelAdvice(HoSoService hoSoService) {
        this.hoSoService = hoSoService;
    }

    @ModelAttribute
    public void addPatientToHeader(Authentication authentication, Model model) {
        model.addAttribute("benhNhan",
                hoSoService.layBenhNhan(hoSoService.layTaiKhoan(authentication)));
    }
}
