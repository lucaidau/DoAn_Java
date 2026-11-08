package vn.carehealth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bac_si")
public class BacSi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tai_khoan_id", nullable = false, unique = true)
    private TaiKhoan taiKhoan;

    @Column(name = "chuyen_khoa_id", nullable = false)
    private Long chuyenKhoaId;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "hoc_ham_hoc_vi", length = 50)
    private String hocHamHocVi;

    @Column(name = "so_nam_kinh_nghiem")
    private Integer soNamKinhNghiem;

    @Column(name = "gioi_thieu", columnDefinition = "nvarchar(max)")
    private String gioiThieu;

    public Long getId() { return id; }
    public TaiKhoan getTaiKhoan() { return taiKhoan; }
    public Long getChuyenKhoaId() { return chuyenKhoaId; }
    public String getHoTen() { return hoTen; }
    public String getHocHamHocVi() { return hocHamHocVi; }
    public Integer getSoNamKinhNghiem() { return soNamKinhNghiem; }
    public String getGioiThieu() { return gioiThieu; }
    public void setHoTen(String hoTen) {
    this.hoTen = hoTen;
}

public void setHocHamHocVi(String hocHamHocVi) {
    this.hocHamHocVi = hocHamHocVi;
}

public void setSoNamKinhNghiem(Integer soNamKinhNghiem) {
    this.soNamKinhNghiem = soNamKinhNghiem;
}

public void setGioiThieu(String gioiThieu) {
    this.gioiThieu = gioiThieu;
}
}
