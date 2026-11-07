package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "chuyen_khoa")
public class ChuyenKhoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_chuyen_khoa")
    private String tenChuyenKhoa;
    @Column(name = "mo_ta")
    private String moTa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenChuyenKhoa() {
        return tenChuyenKhoa;
    }

    public void setTenChuyenKhoa(String tenChuyenKhoa) {
        this.tenChuyenKhoa = tenChuyenKhoa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

}
