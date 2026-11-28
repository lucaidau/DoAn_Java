package com.example.doanjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TinNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_tro_chuyen_id")
    private CuocTroChuyen cuocTroChuyen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nguoi_gui_id")
    private TaiKhoan nguoiGui;

    @Column(name = "noi_dung", length = 1000)
    private String noiDung;

    @Column(name = "thoi_gian_gui")
    private LocalDateTime thoiGianGui;

    @Column(name = "da_doc")
    private Boolean daDoc = false;

    @PrePersist
    void truocKhiLuu() {
        if (thoiGianGui == null) thoiGianGui = LocalDateTime.now();
        if (daDoc == null) daDoc = false;
    }
}
