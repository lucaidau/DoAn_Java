package com.example.doanjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cuoc_tro_chuyen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuocTroChuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tai_khoan_nho_hon_id")
    private TaiKhoan taiKhoanNhoHon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tai_khoan_lon_hon_id")
    private TaiKhoan taiKhoanLonHon;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    public boolean laThanhVien(Long idTaiKhoan)
    {
        if(idTaiKhoan == null)
        {
            return  false;
        }

        boolean idNhoHon = idTaiKhoan.equals(taiKhoanNhoHon.getId());
        boolean idLonHon = idTaiKhoan.equals(taiKhoanLonHon.getId());
        return idLonHon || idNhoHon;
    }

    public TaiKhoan layNguoiDung(Long idNguoiDung) {
        if (idNguoiDung.equals(taiKhoanNhoHon.getId())) {
            return taiKhoanNhoHon;
        }
        return taiKhoanLonHon;
    }

    public TaiKhoan layDoiPhuong(Long idNguoiDung) {
        if (idNguoiDung.equals(taiKhoanNhoHon.getId())) {
            return taiKhoanLonHon;
        }
        return taiKhoanNhoHon;
    }


}
