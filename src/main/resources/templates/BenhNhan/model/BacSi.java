package com.example.baitapluyen.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="bac_si")
public class BacSi {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="tai_khoan_id", nullable=false, unique=true)
    private TaiKhoan taiKhoan;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="chuyen_khoa_id", nullable=false)
    private ChuyenKhoa chuyenKhoa;
    @Column(name="ho_ten", nullable=false, length=100)
    private String hoTen;
    @Column(name="hoc_ham_hoc_vi", length=50)
    private String hocHamHocVi;
    @Column(name="so_nam_kinh_nghiem")
    private Integer soNamKinhNghiem;
    @Column(name="gioi_thieu", columnDefinition="nvarchar(max)")
    private String gioiThieu;
    @OneToMany(mappedBy="bacSi", fetch=FetchType.LAZY)
    private List<LichLamViec> danhSachLichLamViec = new ArrayList<>();
    @OneToMany(mappedBy="bacSi", fetch=FetchType.LAZY)
    private List<LichHen> danhSachLichHen = new ArrayList<>();
    public BacSi() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public TaiKhoan getTaiKhoan(){return taiKhoan;} public void setTaiKhoan(TaiKhoan v){taiKhoan=v;}
    public ChuyenKhoa getChuyenKhoa(){return chuyenKhoa;} public void setChuyenKhoa(ChuyenKhoa v){chuyenKhoa=v;}
    public String getHoTen(){return hoTen;} public void setHoTen(String v){hoTen=v;}
    public String getHocHamHocVi(){return hocHamHocVi;} public void setHocHamHocVi(String v){hocHamHocVi=v;}
    public Integer getSoNamKinhNghiem(){return soNamKinhNghiem;} public void setSoNamKinhNghiem(Integer v){soNamKinhNghiem=v;}
    public String getGioiThieu(){return gioiThieu;} public void setGioiThieu(String v){gioiThieu=v;}
    public List<LichLamViec> getDanhSachLichLamViec(){return danhSachLichLamViec;} public void setDanhSachLichLamViec(List<LichLamViec> v){danhSachLichLamViec=v;}
    public List<LichHen> getDanhSachLichHen(){return danhSachLichHen;} public void setDanhSachLichHen(List<LichHen> v){danhSachLichHen=v;}
}
