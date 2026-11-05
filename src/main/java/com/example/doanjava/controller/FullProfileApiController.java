package com.carehealth.controller;

import com.carehealth.model.Role;
import com.carehealth.service.FullProfileService;
import com.fasterxml.jackson.databind.JsonNode;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
public class FullProfileApiController {
    private final FullProfileService service;
    public FullProfileApiController(FullProfileService service) { this.service = service; }

    @GetMapping("/patient/api/profile")
    public Map<String, Object> patient(Principal principal) { return service.get(principal.getName(), Role.PATIENT); }
    @PutMapping("/patient/api/profile")
    public Map<String, Object> savePatient(Principal principal, @RequestBody JsonNode input) {
        return service.save(principal.getName(), Role.PATIENT, input);
    }
    @GetMapping("/doctor/api/profile")
    public Map<String, Object> doctor(Principal principal) { return service.get(principal.getName(), Role.DOCTOR); }
    @PutMapping("/doctor/api/profile")
    public Map<String, Object> saveDoctor(Principal principal, @RequestBody JsonNode input) {
        return service.save(principal.getName(), Role.DOCTOR, input);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> forbidden(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Không có quyền truy cập."));
    }
}
