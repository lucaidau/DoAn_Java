package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "bac_si")
public class BacSi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ho_ten")
    private String hoTen;
    @Column(name = "hoc_ham_hoc_vi")
    private String hocHamHocVi;
    @Column(name = "so_nam_kinh_nghiem")
    private Integer soNamKinhNghiem;
    @Column(name = "gioi_thieu")
    private String gioiThieu;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tai_khoan_id")
    private TaiKhoan taiKhoan;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chuyen_khoa_id")
    private ChuyenKhoa chuyenKhoa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getHocHamHocVi() {
        return hocHamHocVi;
    }

    public void setHocHamHocVi(String hocHamHocVi) {
        this.hocHamHocVi = hocHamHocVi;
    }

    public Integer getSoNamKinhNghiem() {
        return soNamKinhNghiem;
    }

    public void setSoNamKinhNghiem(Integer soNamKinhNghiem) {
        this.soNamKinhNghiem = soNamKinhNghiem;
    }

    public String getGioiThieu() {
        return gioiThieu;
    }

    public void setGioiThieu(String gioiThieu) {
        this.gioiThieu = gioiThieu;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public ChuyenKhoa getChuyenKhoa() {
        return chuyenKhoa;
    }

    public void setChuyenKhoa(ChuyenKhoa chuyenKhoa) {
        this.chuyenKhoa = chuyenKhoa;
    }

}
