package com.example.baitapluyen.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="lich_hen")
public class LichHen {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="benh_nhan_id", nullable=false)
    private BenhNhan benhNhan;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bac_si_id", nullable=false)
    private BacSi bacSi;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="lich_lam_viec_id", nullable=false)
    private LichLamViec lichLamViec;
    @Column(name="ngay_kham", nullable=false)
    private LocalDate ngayKham;
    @Column(name="khung_gio_kham", nullable=false, length=30)
    private String khungGioKham;
    @Column(name="trieu_chung_benh", columnDefinition="nvarchar(max)")
    private String trieuChungBenh;
    @Column(name="trang_thai", length=30)
    private String trangThai = "CHO_XAC_NHAN";
    @Column(name="ngay_tao")
    private LocalDateTime ngayTao;
    @PrePersist
    public void truocKhiLuu(){ if(ngayTao==null) ngayTao=LocalDateTime.now(); }
    public LichHen() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public BenhNhan getBenhNhan(){return benhNhan;} public void setBenhNhan(BenhNhan v){benhNhan=v;}
    public BacSi getBacSi(){return bacSi;} public void setBacSi(BacSi v){bacSi=v;}
    public LichLamViec getLichLamViec(){return lichLamViec;} public void setLichLamViec(LichLamViec v){lichLamViec=v;}
    public LocalDate getNgayKham(){return ngayKham;} public void setNgayKham(LocalDate v){ngayKham=v;}
    public String getKhungGioKham(){return khungGioKham;} public void setKhungGioKham(String v){khungGioKham=v;}
    public String getTrieuChungBenh(){return trieuChungBenh;} public void setTrieuChungBenh(String v){trieuChungBenh=v;}
    public String getTrangThai(){return trangThai;} public void setTrangThai(String v){trangThai=v;}
    public LocalDateTime getNgayTao(){return ngayTao;} public void setNgayTao(LocalDateTime v){ngayTao=v;}
}
