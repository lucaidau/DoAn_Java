package com.example.doanjava.repository;

import java.util.List;
import java.util.Optional;

import com.example.doanjava.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long>, JpaSpecificationExecutor<TaiKhoan> {
    Optional<TaiKhoan> findByTenDangNhapIgnoreCase(String tenDangNhap);
    Optional<TaiKhoan> findByEmailIgnoreCase(String email);
    List<TaiKhoan> findBySoDienThoai(String soDienThoai);
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsBySoDienThoai(String soDienThoai);

    Page<TaiKhoan> findByTenDangNhapContainingOrEmailContaining(String tenDangNhap, String email, Pageable pageable);
}
