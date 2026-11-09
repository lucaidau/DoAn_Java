package com.example.doanjava.repository;

import java.util.Optional;

import com.example.doanjava.entity.BenhNhan;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BenhNhanRepository extends JpaRepository<BenhNhan, Long> {
    Optional<BenhNhan> findFirstByTaiKhoanId(Long taiKhoanId);
}
