package com.example.doanjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@Entity
@Table(name = "lich_hen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "benh_nhan_id")
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lich_lam_viec_id")
    private LichLamViec lichLamViec;
}
