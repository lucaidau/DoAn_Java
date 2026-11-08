package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "lich_lam_viec")
public class LichLamViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ngay_lam_viec")
    private LocalDate ngayLamViec;
    @Column(name = "ten_ca")
    private String tenCa;
    @Column(name = "gio_bat_dau")
    private LocalTime gioBatDau;
    @Column(name = "gio_ket_thuc")
    private LocalTime gioKetThuc;
    @Column(name = "so_luong_toida")
    private Integer soLuongToida;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bac_si_id")
    private BacSi bacSi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getNgayLamViec() {
        return ngayLamViec;
    }

    public void setNgayLamViec(LocalDate ngayLamViec) {
        this.ngayLamViec = ngayLamViec;
    }

    public String getTenCa() {
        return tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public LocalTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public Integer getSoLuongToida() {
        return soLuongToida;
    }

    public void setSoLuongToida(Integer soLuongToida) {
        this.soLuongToida = soLuongToida;
    }

    public BacSi getBacSi() {
        return bacSi;
    }

    public void setBacSi(BacSi bacSi) {
        this.bacSi = bacSi;
    }

}
