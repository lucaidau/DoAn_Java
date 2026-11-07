package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "lich_hen")
public class LichHen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ngay_kham")
    private LocalDate ngayKham;
    @Column(name = "khung_gio_kham")
    private String khungGioKham;
    @Column(name = "trieu_chung_benh")
    private String trieuChungBenh;
    @Column(name = "ghi_chu_tiep_nhan")
    private String ghiChuTiepNhan;
    public String getGhiChuTiepNhan() { return ghiChuTiepNhan; }
    public void setGhiChuTiepNhan(String value) { this.ghiChuTiepNhan = value; }
    @Column(name = "trang_thai")
    private String trangThai;
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "benh_nhan_id")
    private BenhNhan benhNhan;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bac_si_id")
    private BacSi bacSi;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lich_lam_viec_id")
    private LichLamViec lichLamViec;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phong_kham_id")
    private PhongKham phongKham;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dich_vu_kham_id")
    private DichVuKham dichVuKham;

    public PhongKham getPhongKham() {
        return phongKham;
    }

    public void setPhongKham(PhongKham phongKham) {
        this.phongKham = phongKham;
    }

    public DichVuKham getDichVuKham() {
        return dichVuKham;
    }

    public void setDichVuKham(DichVuKham dichVuKham) {
        this.dichVuKham = dichVuKham;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getNgayKham() {
        return ngayKham;
    }

    public void setNgayKham(LocalDate ngayKham) {
        this.ngayKham = ngayKham;
    }

    public String getKhungGioKham() {
        return khungGioKham;
    }

    public void setKhungGioKham(String khungGioKham) {
        this.khungGioKham = khungGioKham;
    }

    public String getTrieuChungBenh() {
        return trieuChungBenh;
    }

    public void setTrieuChungBenh(String trieuChungBenh) {
        this.trieuChungBenh = trieuChungBenh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public BacSi getBacSi() {
        return bacSi;
    }

    public void setBacSi(BacSi bacSi) {
        this.bacSi = bacSi;
    }

    public LichLamViec getLichLamViec() {
        return lichLamViec;
    }

    public void setLichLamViec(LichLamViec lichLamViec) {
        this.lichLamViec = lichLamViec;
    }

}
