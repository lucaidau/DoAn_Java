package com.example.doanjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@Entity
@Table(name = "lich_lam_viec")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichLamViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ngay_lam_viec")
    private LocalDate ngayLamViec;

    @Column(name = "ten_ca")
    private String tenCa;

    @Column(name = "gio_bat_dau")
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc")
    private LocalTime gioKetThuc;

    @Column(name = "so_luong_toida")
    private Integer soLuongToida;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bac_si_id")
    private BacSi bacSi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phong_kham_id")
    private PhongKham phongKham;
}
