package com.example.doanjava.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BacSiDTO {
    private String tenDangNhap;
    private String matKhau;
    private String email;
    private String soDienThoai;

    private String hoTen;
    private Long chuyenKhoaId;
    private String hocHamHocVi;
    private Integer soNamKinhNghiem;
    private String gioiThieu;
    private Boolean trangThai = true;
}
