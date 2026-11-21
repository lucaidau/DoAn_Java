package com.example.doanjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@Entity
@Table(name = "benh_nhan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BenhNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "tien_su_benh")
    private String tienSuBenh;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tai_khoan_id")
    private TaiKhoan taiKhoan;

    @Column(name = "nhom_mau")
    private String nhomMau;

    @Column(name = "so_cccd")
    private String soCccd;

    @Column(name = "ma_bhyt")
    private String maBhyt;

    @Column(name = "tien_su_gia_dinh")
    private String tienSuGiaDinh;

    @Column(name = "thoi_quen_sinh_hoat")
    private String thoiQuenSinhHoat;


}
