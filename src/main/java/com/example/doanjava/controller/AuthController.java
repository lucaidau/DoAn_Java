package com.carehealth.controller;
import com.carehealth.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
@Controller public class AuthController {
 private final AccountService service;
 public AuthController(AccountService service){this.service=service;}
 @GetMapping("/") public String root(){return "redirect:/login";}
 @GetMapping("/login")
public String login() {
    return "DangNhap";
}

@GetMapping("/register")
public String register() {
    return "DangKi";
}
 @PostMapping("/register") public String registerPost(@RequestParam String fullName,@RequestParam String phone,@RequestParam(required=false) String email,@RequestParam String password,@RequestParam String confirmPassword,@RequestParam LocalDate birthDate,@RequestParam String gender,@RequestParam(defaultValue="false") boolean terms,RedirectAttributes redirect){
  try {if(!terms)throw new IllegalArgumentException("Bạn cần đồng ý với điều khoản.");service.registerPatient(fullName,phone,email,password,confirmPassword,birthDate,gender);return "redirect:/login?registered";}
  catch(IllegalArgumentException e){redirect.addFlashAttribute("registerError",e.getMessage());return "redirect:/register";}
 }
 @GetMapping("/denied") public String denied(){return "denied";}
}
