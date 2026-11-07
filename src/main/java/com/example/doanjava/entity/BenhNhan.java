package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "benh_nhan")
public class BenhNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ho_ten")
    private String hoTen;
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    @Column(name = "gioi_tinh")
    private String gioiTinh;
    @Column(name = "dia_chi")
    private String diaChi;
    @Column(name = "tien_su_benh")
    private String tienSuBenh;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tai_khoan_id")
    private TaiKhoan taiKhoan;

    @Column(name = "nhom_mau")
    private String nhomMau;
    @Column(name = "so_cccd")
    private String soCccd;
    @Column(name = "ma_bhyt")
    private String maBhyt;

    public String getNhomMau() {
        return nhomMau;
    }

    public void setNhomMau(String nhomMau) {
        this.nhomMau = nhomMau;
    }

    public String getSoCccd() {
        return soCccd;
    }

    public void setSoCccd(String soCccd) {
        this.soCccd = soCccd;
    }

    public String getMaBhyt() {
        return maBhyt;
    }

    public void setMaBhyt(String maBhyt) {
        this.maBhyt = maBhyt;
    }

    @Column(name = "tien_su_gia_dinh")
    private String tienSuGiaDinh;
    @Column(name = "thoi_quen_sinh_hoat")
    private String thoiQuenSinhHoat;
    public String getTienSuGiaDinh() { return tienSuGiaDinh; }
    public void setTienSuGiaDinh(String value) { this.tienSuGiaDinh = value; }
    public String getThoiQuenSinhHoat() { return thoiQuenSinhHoat; }
    public void setThoiQuenSinhHoat(String value) { this.thoiQuenSinhHoat = value; }
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

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getTienSuBenh() {
        return tienSuBenh;
    }

    public void setTienSuBenh(String tienSuBenh) {
        this.tienSuBenh = tienSuBenh;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

}
