package com.example.doanjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@Entity
@Table(name = "ho_so_benh_an")
@Getter
@Setter
@NoArgsConstructor
public class HoSoBenhAn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chan_doan")
    private String chanDoan;

    @Column(name = "loi_dan_bac_si")
    private String loiDanBacSi;

    @Column(name = "kham_tim_mach")
    private String khamTimMach;

    @Column(name = "kham_ho_hap")
    private String khamHoHap;

    @Column(name = "kham_tieu_hoa")
    private String khamTieuHoa;

    @Column(name = "chan_doan_phu")
    private String chanDoanPhu;

    @Column(name = "can_lam_sang")
    private String canLamSang;

    @Column(name = "huong_dieu_tri")
    private String huongDieuTri;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lich_hen_id")
    private LichHen lichHen;



}
