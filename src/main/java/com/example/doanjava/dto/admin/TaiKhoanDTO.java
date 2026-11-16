package com.example.doanjava.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanDTO {
    private long id;
    private String hoTen;
    private String vaiTro;
    private String email;
    private String soDienThoai;
    private LocalDateTime ngayTao;
    private boolean trangThai;

}
