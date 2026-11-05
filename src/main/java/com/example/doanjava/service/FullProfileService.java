package com.carehealth.service;

import com.carehealth.model.*;
import com.carehealth.repository.ProfileSettingsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class FullProfileService {
    private final AccountService accounts;
    private final ProfileSettingsRepository settingsRepository;
    private final ProfileService profiles;
    private final ObjectMapper mapper;
    private static final Set<String> PATIENT_FIELDS = Set.of(
        "address", "bloodType", "allergies", "medicalHistory", "healthInsuranceNumber",
        "emergencyContactName", "emergencyContactPhone", "emergencyContactRelation",
        "citizenId", "insuranceHospital", "insuranceExpiry", "insuranceStart");
    private static final Set<String> DOCTOR_FIELDS = Set.of(
        "displayName", "degree", "specialty", "contactEmail", "contactPhone", "licenseNumber",
        "introduction", "tags", "educationItems", "hospital", "workAddress", "workplace",
        "consultFee", "videoFee", "healthInsurance", "videoEnabled", "weekdayMorning",
        "weekdayAfternoon", "saturdayMorning", "saturdayAfternoon", "duration",
        "maxPatients", "acceptingPatients", "autoApprove", "emergencySms", "avatarData");

    public FullProfileService(AccountService accounts, ProfileSettingsRepository settingsRepository,
                              ProfileService profiles, ObjectMapper mapper) {
        this.accounts = accounts;
        this.settingsRepository = settingsRepository;
        this.profiles = profiles;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> get(String username, Role role) {
        Account account = checked(username, role);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", account.getId());
        response.put("fullName", account.getFullName());
        response.put("phone", account.getPhone());
        response.put("email", account.getEmail() == null ? "" : account.getEmail());
        response.put("birthDate", account.getBirthDate());
        response.put("gender", account.getGender());
        String json = settingsRepository.findById(account.getId()).map(ProfileSettings::getSettingsJson).orElse("{}");
        try {
            JsonNode node = mapper.readTree(json);
            response.put("fields", node);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Không đọc được dữ liệu hồ sơ", ex);
        }
        return response;
    }

    @Transactional
    public Map<String, Object> save(String username, Role role, JsonNode data) {
        Account account = checked(username, role);
        if (data == null || !data.isObject()) throw new IllegalArgumentException("Dữ liệu phải là một đối tượng JSON.");
        Set<String> allowed = role == Role.PATIENT ? PATIENT_FIELDS : DOCTOR_FIELDS;
        ObjectNode result = mapper.createObjectNode();
        Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            if (!allowed.contains(key)) throw new IllegalArgumentException("Trường không hợp lệ: " + key);
            JsonNode value = field.getValue();
            if (value.isTextual()) {
                String v = value.asText().trim();
                int max = key.equals("avatarData") ? 360000 : key.equals("introduction") ? 3800 : key.equals("educationItems") ? 4000 : 500;
                if (v.length() > max) throw new IllegalArgumentException(key + " vượt quá " + max + " ký tự.");
                result.put(key, v);
            } else if (value.isBoolean()) result.put(key, value.asBoolean());
            else if (value.isIntegralNumber()) result.put(key, value.asLong());
            else if (value.isNull()) result.putNull(key);
            else throw new IllegalArgumentException("Giá trị không hợp lệ: " + key);
        }
        if (role == Role.PATIENT) validatePatient(result);
        else validateDoctor(result);
        ProfileSettings setting = settingsRepository.findById(account.getId())
            .orElseGet(() -> new ProfileSettings(account));
        setting.setSettingsJson(result.toString());
        settingsRepository.save(setting);
        return get(username, role);
    }

    private void validatePatient(ObjectNode node) {
        String blood = node.path("bloodType").asText("");
        if (!Set.of("", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-").contains(blood))
            throw new IllegalArgumentException("Nhóm máu không hợp lệ.");
        String phone = node.path("emergencyContactPhone").asText("").replaceAll("[\\s.-]", "");
        if (!phone.isEmpty() && !phone.matches("0[0-9]{9}"))
            throw new IllegalArgumentException("Số điện thoại khẩn cấp phải gồm 10 số, bắt đầu bằng 0.");
        node.put("emergencyContactPhone", phone);
        String expiry = node.path("insuranceExpiry").asText("");
        String start = node.path("insuranceStart").asText("");
        try {
            if (!expiry.isBlank()) java.time.LocalDate.parse(expiry);
            if (!start.isBlank()) java.time.LocalDate.parse(start);
            if (!expiry.isBlank() && !start.isBlank() && java.time.LocalDate.parse(expiry).isBefore(java.time.LocalDate.parse(start)))
                throw new IllegalArgumentException("Ngày hết hạn BHYT không thể trước ngày bắt đầu.");
        } catch (java.time.format.DateTimeParseException ex) {
            throw new IllegalArgumentException("Ngày BHYT cần theo định dạng yyyy-MM-dd.");
        }
    }
    private void validateDoctor(ObjectNode node) {
        String avatar=node.path("avatarData").asText("");
        if(!avatar.isBlank()) {
            if(!avatar.matches("^data:image/(png|jpeg|webp);base64,[A-Za-z0-9+/=]+$"))
                throw new IllegalArgumentException("Ảnh đại diện phải là PNG, JPEG hoặc WebP.");
            try {
                byte[] bytes=java.util.Base64.getDecoder().decode(avatar.substring(avatar.indexOf(',')+1));
                if(bytes.length>250000)throw new IllegalArgumentException("Ảnh đại diện tối đa 250 KB.");
                boolean png=bytes.length>8 && (bytes[0]&255)==137 && (bytes[1]&255)==80 && (bytes[2]&255)==78 && (bytes[3]&255)==71;
                boolean jpg=bytes.length>3 && (bytes[0]&255)==255 && (bytes[1]&255)==216 && (bytes[2]&255)==255;
                boolean webp=bytes.length>12 && new String(bytes,0,4,java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF") && new String(bytes,8,4,java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP");
                if(!(png||jpg||webp))throw new IllegalArgumentException("Nội dung tệp không phải ảnh được hỗ trợ.");
            } catch(IllegalArgumentException ex) { throw ex; }
        }
        String email = node.path("contactEmail").asText("");
        if (!email.isBlank() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Email công tác không hợp lệ.");
        String phone = node.path("contactPhone").asText("").replaceAll("[\\s.-]", "");
        if (!phone.isBlank() && !phone.matches("0[0-9]{9}"))
            throw new IllegalArgumentException("Điện thoại bác sĩ phải có 10 số, bắt đầu bằng 0.");
        node.put("contactPhone", phone);
        for (String key : List.of("consultFee", "videoFee", "maxPatients")) {
            JsonNode v = node.get(key);
            if (v != null && !v.isNull()) {
                if (!v.isIntegralNumber() || v.asLong() < 0 || v.asLong() > (key.equals("maxPatients") ? 100 : 100000000))
                    throw new IllegalArgumentException(key + " phải là số nguyên hợp lệ không âm.");
            }
        }
        int duration = node.path("duration").asInt(30);
        if (!Set.of(20, 30, 45).contains(duration)) throw new IllegalArgumentException("Thời lượng ca khám không hợp lệ.");
    }
    private Account checked(String username, Role role) {
        Account a = accounts.current(username);
        if (a.getRole() != role) throw new AccessDeniedException("Không có quyền thao tác hồ sơ này.");
        return a;
    }
}
