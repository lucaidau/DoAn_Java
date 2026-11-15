package com.example.baitapluyen.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tai_khoan")
public class TaiKhoan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="ten_dang_nhap", nullable=false, unique=true, length=50)
    private String tenDangNhap;
    @Column(name="mat_khau", nullable=false, length=255)
    private String matKhau;
    @Column(nullable=false, unique=true, length=100)
    private String email;
    @Column(name="so_dien_thoai", length=15)
    private String soDienThoai;
    @Column(name="vai_tro", nullable=false, length=20)
    private String vaiTro;
    @Column(name="trang_thai")
    private Boolean trangThai = true;
    public TaiKhoan() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getTenDangNhap(){return tenDangNhap;} public void setTenDangNhap(String v){tenDangNhap=v;}
    public String getMatKhau(){return matKhau;} public void setMatKhau(String v){matKhau=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getSoDienThoai(){return soDienThoai;} public void setSoDienThoai(String v){soDienThoai=v;}
    public String getVaiTro(){return vaiTro;} public void setVaiTro(String v){vaiTro=v;}
    public Boolean getTrangThai(){return trangThai;} public void setTrangThai(Boolean v){trangThai=v;}
}
