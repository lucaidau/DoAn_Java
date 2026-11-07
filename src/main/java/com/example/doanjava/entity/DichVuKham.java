package com.example.demo.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "dich_vu_kham")
public class DichVuKham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ten_dich_vu")
    private String tenDichVu;
    @Column(name = "gia_kham")
    private BigDecimal giaKham;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public BigDecimal getGiaKham() {
        return giaKham;
    }

    public void setGiaKham(BigDecimal giaKham) {
        this.giaKham = giaKham;
    }
}
