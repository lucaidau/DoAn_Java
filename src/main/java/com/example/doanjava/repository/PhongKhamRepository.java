package com.example.doanjava.repository;

import com.example.doanjava.entity.PhongKham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhongKhamRepository extends JpaRepository<PhongKham, Long> {
    List<PhongKham> findByChuyenKhoa_IdIn(List<Long> chuyenKhoaIds);
}
