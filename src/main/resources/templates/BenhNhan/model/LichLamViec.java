package com.example.baitapluyen.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="lich_lam_viec")
public class LichLamViec {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bac_si_id", nullable=false)
    private BacSi bacSi;
    @Column(name="ngay_lam_viec", nullable=false)
    private LocalDate ngayLamViec;
    @Column(name="ten_ca", nullable=false, length=20)
    private String tenCa;
    @Column(name="gio_bat_dau", nullable=false)
    private LocalTime gioBatDau;
    @Column(name="gio_ket_thuc", nullable=false)
    private LocalTime gioKetThuc;
    @Column(name="so_luong_toida")
    private Integer soLuongToiDa = 10;
    @OneToMany(mappedBy="lichLamViec", fetch=FetchType.LAZY)
    private List<LichHen> danhSachLichHen = new ArrayList<>();
    public LichLamViec() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public BacSi getBacSi(){return bacSi;} public void setBacSi(BacSi v){bacSi=v;}
    public LocalDate getNgayLamViec(){return ngayLamViec;} public void setNgayLamViec(LocalDate v){ngayLamViec=v;}
    public String getTenCa(){return tenCa;} public void setTenCa(String v){tenCa=v;}
    public LocalTime getGioBatDau(){return gioBatDau;} public void setGioBatDau(LocalTime v){gioBatDau=v;}
    public LocalTime getGioKetThuc(){return gioKetThuc;} public void setGioKetThuc(LocalTime v){gioKetThuc=v;}
    public Integer getSoLuongToiDa(){return soLuongToiDa;} public void setSoLuongToiDa(Integer v){soLuongToiDa=v;}
    public List<LichHen> getDanhSachLichHen(){return danhSachLichHen;} public void setDanhSachLichHen(List<LichHen> v){danhSachLichHen=v;}
}
