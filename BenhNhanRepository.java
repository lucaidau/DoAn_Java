package com.example.doanjava.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.doanjava.entity.BenhNhan;

public interface BenhNhanRepository extends JpaRepository<BenhNhan, Long> {
    Optional<com.example.doanjava.entity.BenhNhan> findFirstByTaiKhoanId(Long taiKhoanId);
}
