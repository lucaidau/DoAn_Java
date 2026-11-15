package com.example.doanjava.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "don_thuoc")
public class DonThuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_thuoc")
    private String tenThuoc;
    @Column(name = "lieu_luong")
    private String lieuLuong;
    @Column(name = "so_luong")
    private Integer soLuong;
    @Column(name = "huong_dan_su_dung")
    private String huongDanSuDung;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ho_so_benh_an_id")
    private HoSoBenhAn hoSoBenhAn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public String getLieuLuong() {
        return lieuLuong;
    }

    public void setLieuLuong(String lieuLuong) {
        this.lieuLuong = lieuLuong;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public String getHuongDanSuDung() {
        return huongDanSuDung;
    }

    public void setHuongDanSuDung(String huongDanSuDung) {
        this.huongDanSuDung = huongDanSuDung;
    }

    public HoSoBenhAn getHoSoBenhAn() {
        return hoSoBenhAn;
    }

    public void setHoSoBenhAn(HoSoBenhAn hoSoBenhAn) {
        this.hoSoBenhAn = hoSoBenhAn;
    }

}
