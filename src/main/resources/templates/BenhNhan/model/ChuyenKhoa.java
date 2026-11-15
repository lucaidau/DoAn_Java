package com.example.baitapluyen.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="chuyen_khoa")
public class ChuyenKhoa {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="ten_chuyen_khoa", nullable=false, length=100)
    private String tenChuyenKhoa;
    @Column(name="mo_ta", columnDefinition="nvarchar(max)")
    private String moTa;
    @OneToMany(mappedBy="chuyenKhoa", fetch=FetchType.LAZY)
    private List<BacSi> danhSachBacSi = new ArrayList<>();
    public ChuyenKhoa() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getTenChuyenKhoa(){return tenChuyenKhoa;} public void setTenChuyenKhoa(String v){tenChuyenKhoa=v;}
    public String getMoTa(){return moTa;} public void setMoTa(String v){moTa=v;}
    public List<BacSi> getDanhSachBacSi(){return danhSachBacSi;} public void setDanhSachBacSi(List<BacSi> v){danhSachBacSi=v;}
}
