package com.example.doanjava.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuaBacSiDTO {
    private Long id;
    private String hoTen;
    private Long chuyenKhoaId;
    private String hocHamHocVi;
    private Integer soNamKinhNghiem;
    private String gioiThieu;
    private Boolean trangThai;

    private String emailMoi;
    private String soDienThoaiMoi;
    private String tenDangNhapMoi;
    private String matKhauMoi;
}
