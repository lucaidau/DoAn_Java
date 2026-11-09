package com.example.doanjava.repository;

import java.util.List;
import java.util.Optional;

import com.example.doanjava.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long> {
    Optional<TaiKhoan> findByTenDangNhapIgnoreCase(String tenDangNhap);
    Optional<TaiKhoan> findByEmailIgnoreCase(String email);
    List<TaiKhoan> findBySoDienThoai(String soDienThoai);
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsBySoDienThoai(String soDienThoai);
}
