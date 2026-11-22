package com.example.doanjava.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "phong_kham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhongKham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_phong")
    private String tenPhong;

    @Column(name = "khu_vuc")
    private String khuVuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chuyen_khoa_id")
    private ChuyenKhoa chuyenKhoa;
}
