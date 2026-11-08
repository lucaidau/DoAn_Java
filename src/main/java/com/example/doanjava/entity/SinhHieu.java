
package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sinh_hieu")
public class SinhHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_hen_id", nullable = false, unique = true)
    private LichHen lichHen;
    @Column(name = "huyet_ap_tam_thu")
    private Integer huyetApTamThu;
    @Column(name = "huyet_ap_tam_truong")
    private Integer huyetApTamTruong;
    @Column(name = "nhip_tim")
    private Integer nhipTim;
    @Column(name = "spo2")
    private Integer spo2;
    @Column(name = "can_nang")
    private BigDecimal canNang;
    @Column(name = "chieu_cao")
    private BigDecimal chieuCao;
    @Column(name = "thoi_gian_do")
    private LocalDateTime thoiGianDo;

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

    public Integer getHuyetApTamThu() {
        return huyetApTamThu;
    }

    public void setHuyetApTamThu(Integer huyetApTamThu) {
        this.huyetApTamThu = huyetApTamThu;
    }

    public Integer getHuyetApTamTruong() {
        return huyetApTamTruong;
    }

    public void setHuyetApTamTruong(Integer huyetApTamTruong) {
        this.huyetApTamTruong = huyetApTamTruong;
    }

    public Integer getNhipTim() {
        return nhipTim;
    }

    public void setNhipTim(Integer nhipTim) {
        this.nhipTim = nhipTim;
    }

    public Integer getSpo2() {
        return spo2;
    }

    public void setSpo2(Integer spo2) {
        this.spo2 = spo2;
    }

    public BigDecimal getCanNang() {
        return canNang;
    }

    public void setCanNang(BigDecimal canNang) {
        this.canNang = canNang;
    }

    public BigDecimal getChieuCao() {
        return chieuCao;
    }

    public void setChieuCao(BigDecimal chieuCao) {
        this.chieuCao = chieuCao;
    }

    public LocalDateTime getThoiGianDo() {
        return thoiGianDo;
    }

    public void setThoiGianDo(LocalDateTime thoiGianDo) {
        this.thoiGianDo = thoiGianDo;
    }

    // -- Tính BMI từ cân nặng và chiều cao trong database --
    @Transient
    public Double getBmi() {
        if (canNang == null || chieuCao == null || chieuCao.doubleValue() <= 0) {
            return null;
        }
        double chieuCaoMet = chieuCao.doubleValue() / 100.0;
        return Math.round(canNang.doubleValue() / (chieuCaoMet * chieuCaoMet) * 10.0) / 10.0;
    }
}
