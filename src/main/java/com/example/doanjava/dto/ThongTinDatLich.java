package com.example.doanjava.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ThongTinDatLich {
    @NotNull(message = "Vui lòng chọn bác sĩ.")
    private Long bacSiId;

    @NotNull(message = "Vui lòng chọn ca làm việc.")
    private Long lichLamViecId;

    @NotNull(message = "Ngày khám không hợp lệ.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayKham;

    @NotBlank(message = "Vui lòng chọn khung giờ khám.")
    private String khungGioKham;

    @Size(max = 2000, message = "Triệu chứng không được vượt quá 2000 ký tự.")
    private String trieuChung = "";

    public Long getBacSiId() { return bacSiId; }
    public void setBacSiId(Long bacSiId) { this.bacSiId = bacSiId; }
    public Long getLichLamViecId() { return lichLamViecId; }
    public void setLichLamViecId(Long lichLamViecId) { this.lichLamViecId = lichLamViecId; }
    public LocalDate getNgayKham() { return ngayKham; }
    public void setNgayKham(LocalDate ngayKham) { this.ngayKham = ngayKham; }
    public String getKhungGioKham() { return khungGioKham; }
    public void setKhungGioKham(String khungGioKham) { this.khungGioKham = khungGioKham; }
    public String getTrieuChung() { return trieuChung; }
    public void setTrieuChung(String trieuChung) { this.trieuChung = trieuChung; }
}
