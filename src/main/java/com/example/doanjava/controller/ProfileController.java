package com.carehealth.controller;

import com.carehealth.service.AccountService;
import com.carehealth.service.ProfileService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    private final AccountService accounts;
    private final ProfileService profiles;

    public ProfileController(AccountService accounts, ProfileService profiles) {
        this.accounts = accounts;
        this.profiles = profiles;
    }

    @GetMapping("/patient/home")
    public String patientHome() {
        return "redirect:/patient/profile";
    }

    @GetMapping("/patient/profile")
    public String patientProfile(Principal principal, Model model) {
        model.addAttribute("account", accounts.current(principal.getName()));
        model.addAttribute("profile", profiles.patient(principal.getName()));
        model.addAttribute("bloodTypes", java.util.List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        return "hosobenhnnhan";
    }

    @PostMapping("/patient/profile")
    public String updatePatient(Principal principal,
                                @RequestParam(defaultValue = "") String address,
                                @RequestParam(defaultValue = "") String bloodType,
                                @RequestParam(defaultValue = "") String allergies,
                                @RequestParam(defaultValue = "") String medicalHistory,
                                @RequestParam(defaultValue = "") String healthInsuranceNumber,
                                @RequestParam(defaultValue = "") String emergencyContactName,
                                @RequestParam(defaultValue = "") String emergencyContactPhone,
                                RedirectAttributes redirect) {
        try {
            profiles.updatePatient(principal.getName(), address, bloodType, allergies,
                    medicalHistory, healthInsuranceNumber, emergencyContactName, emergencyContactPhone);
            redirect.addFlashAttribute("success", "Đã cập nhật hồ sơ bệnh nhân.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/patient/profile";
    }

    @GetMapping("/doctor/profile")
    public String doctorProfile(Principal principal, Model model) {
        model.addAttribute("account", accounts.current(principal.getName()));
        model.addAttribute("profile", profiles.doctor(principal.getName()));
        return "HoSoBacSi";
    }

    @PostMapping("/doctor/profile")
    public String updateDoctor(Principal principal,
                               @RequestParam(defaultValue = "") String specialty,
                               @RequestParam(defaultValue = "") String hospital,
                               @RequestParam(defaultValue = "") String workplace,
                               @RequestParam(required = false) Integer experienceYears,
                               @RequestParam(defaultValue = "") String introduction,
                               @RequestParam(defaultValue = "") String education,
                               RedirectAttributes redirect) {
        try {
            profiles.updateDoctor(principal.getName(), specialty, hospital, workplace,
                    experienceYears, introduction, education);
            redirect.addFlashAttribute("success", "Đã cập nhật hồ sơ bác sĩ.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/doctor/profile";
    }

    @GetMapping("/doctor/detail")
    public String detail(Principal principal, Model model) {
        model.addAttribute("account", accounts.current(principal.getName()));
        return "redirect:/doctors/" + accounts.current(principal.getName()).getId();
    }
}
