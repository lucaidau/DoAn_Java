package com.example.doanjava.repository;

import java.util.Optional;

import com.example.doanjava.entity.BacSi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BacSiRepository extends JpaRepository<BacSi, Long> {
    Optional<BacSi> findByTaiKhoanId(Long taiKhoanId);
}
