package com.example.demo.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "thuoc")
public class Thuoc {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_thuoc")
    private String tenThuoc;
    @Column(name = "don_vi")
    private String donVi;
    public Long getId() { return id; }
    public String getTenThuoc() { return tenThuoc; }
    public String getDonVi() { return donVi; }
}
