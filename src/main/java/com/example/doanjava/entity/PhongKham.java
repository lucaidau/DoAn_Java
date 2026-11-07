package com.example.demo.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "phong_kham")
public class PhongKham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_phong")
    private String tenPhong;
    @Column(name = "khu_vuc")
    private String khuVuc;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(String khuVuc) {
        this.khuVuc = khuVuc;
    }
}
