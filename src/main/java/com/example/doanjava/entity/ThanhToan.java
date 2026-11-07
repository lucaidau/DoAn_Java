
package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "thanh_toan")
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_hen_id", nullable = false, unique = true)
    private LichHen lichHen;
    @Column(name = "so_tien", nullable = false)
    private BigDecimal soTien;
    @Column(name = "trang_thai", nullable = false)
    private String trangThai;
    @Column(name = "phuong_thuc")
    private String phuongThuc;
    @Column(name = "ma_giao_dich")
    private String maGiaoDich;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }
}
