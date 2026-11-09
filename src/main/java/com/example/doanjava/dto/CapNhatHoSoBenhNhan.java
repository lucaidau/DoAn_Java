package com.example.doanjava.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class CapNhatHoSoBenhNhan {
    @NotBlank(message = "Vui lòng nhập họ tên.")
    @Size(max = 100, message = "Họ tên không quá 100 ký tự.")
    private String hoTen;

    @NotNull(message = "Vui lòng chọn ngày sinh.")
    @Past(message = "Ngày sinh phải trước ngày hiện tại.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngaySinh;

    @NotBlank(message = "Vui lòng chọn giới tính.")
    @Pattern(regexp = "Nam|Nữ|Khác", message = "Giới tính không hợp lệ.")
    private String gioiTinh;

    @NotBlank(message = "Vui lòng nhập số điện thoại.")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.")
    private String soDienThoai;

    @NotBlank(message = "Vui lòng nhập email.")
    @Email(message = "Email không hợp lệ.")
    @Size(max = 100, message = "Email không quá 100 ký tự.")
    private String email;

    @Pattern(regexp = "|A[+-]|B[+-]|AB[+-]|O[+-]", message = "Nhóm máu không hợp lệ.")
    private String nhomMau = "";

    @Size(max = 255, message = "Địa chỉ không quá 255 ký tự.")
    private String diaChi = "";

    private String tienSuBenh = "";

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNhomMau() { return nhomMau; }
    public void setNhomMau(String nhomMau) { this.nhomMau = nhomMau; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getTienSuBenh() { return tienSuBenh; }
    public void setTienSuBenh(String tienSuBenh) { this.tienSuBenh = tienSuBenh; }
}
