package com.example.doanjava.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "ho_so_benh_an")
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

    public String getKhamTimMach() { return khamTimMach; }
    public void setKhamTimMach(String value) { this.khamTimMach = value; }
    public String getKhamHoHap() { return khamHoHap; }
    public void setKhamHoHap(String value) { this.khamHoHap = value; }
    public String getKhamTieuHoa() { return khamTieuHoa; }
    public void setKhamTieuHoa(String value) { this.khamTieuHoa = value; }
    public String getChanDoanPhu() { return chanDoanPhu; }
    public void setChanDoanPhu(String value) { this.chanDoanPhu = value; }
    public String getCanLamSang() { return canLamSang; }
    public void setCanLamSang(String value) { this.canLamSang = value; }
    public String getHuongDieuTri() { return huongDieuTri; }
    public void setHuongDieuTri(String value) { this.huongDieuTri = value; }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public String getLoiDanBacSi() {
        return loiDanBacSi;
    }

    public void setLoiDanBacSi(String loiDanBacSi) {
        this.loiDanBacSi = loiDanBacSi;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

}
