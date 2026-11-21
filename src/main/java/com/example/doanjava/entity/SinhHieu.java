
package com.example.doanjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sinh_hieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SinhHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_hen_id", nullable = false, unique = true)
    private LichHen lichHen;

    @Column(name = "huyet_ap_tam_thu")
    private Integer huyetApTamThu;

    @Column(name = "huyet_ap_tam_truong")
    private Integer huyetApTamTruong;

    @Column(name = "nhip_tim")
    private Integer nhipTim;

    @Column(name = "spo2")
    private Integer spo2;

    @Column(name = "can_nang", columnDefinition = "DECIMAL(6,2)")
    private BigDecimal canNang;

    @Column(name = "chieu_cao", columnDefinition = "DECIMAL(6,2)")
    private BigDecimal chieuCao;

    @Column(name = "thoi_gian_do")
    private LocalDateTime thoiGianDo;



    // -- Tính BMI từ cân nặng và chiều cao trong database --
    @Transient
    public Double getBmi() {
        if (canNang == null || chieuCao == null || chieuCao.doubleValue() <= 0) {
            return null;
        }
        double chieuCaoMet = chieuCao.doubleValue() / 100.0;
        return Math.round(canNang.doubleValue() / (chieuCaoMet * chieuCaoMet) * 10.0) / 10.0;
    }
}
