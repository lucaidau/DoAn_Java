package com.example.baitapluyen.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="benh_nhan")
public class BenhNhan {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="tai_khoan_id", nullable=false)
    private TaiKhoan taiKhoan;
    @Column(name="ho_ten", nullable=false, length=100)
    private String hoTen;
    @Column(name="ngay_sinh")
    private LocalDate ngaySinh;
    @Column(name="gioi_tinh", length=10)
    private String gioiTinh;
    @Column(name="dia_chi", length=255)
    private String diaChi;
    @Column(name="tien_su_benh", columnDefinition="nvarchar(max)")
    private String tienSuBenh;
    @OneToMany(mappedBy="benhNhan", fetch=FetchType.LAZY)
    private List<LichHen> danhSachLichHen = new ArrayList<>();
    public BenhNhan() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public TaiKhoan getTaiKhoan(){return taiKhoan;} public void setTaiKhoan(TaiKhoan v){taiKhoan=v;}
    public String getHoTen(){return hoTen;} public void setHoTen(String v){hoTen=v;}
    public LocalDate getNgaySinh(){return ngaySinh;} public void setNgaySinh(LocalDate v){ngaySinh=v;}
    public String getGioiTinh(){return gioiTinh;} public void setGioiTinh(String v){gioiTinh=v;}
    public String getDiaChi(){return diaChi;} public void setDiaChi(String v){diaChi=v;}
    public String getTienSuBenh(){return tienSuBenh;} public void setTienSuBenh(String v){tienSuBenh=v;}
    public List<LichHen> getDanhSachLichHen(){return danhSachLichHen;} public void setDanhSachLichHen(List<LichHen> v){danhSachLichHen=v;}
}
