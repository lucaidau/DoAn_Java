package com.example.doanjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@Entity
@Table(name = "chuyen_khoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChuyenKhoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_chuyen_khoa")
    private String tenChuyenKhoa;

    @Column(name = "mo_ta")
    private String moTa;


}
