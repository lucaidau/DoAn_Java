package com.example.doanjava.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.doanjava.entity.BacSi;

public interface BacSiRepository extends JpaRepository<BacSi, Long>,
        JpaSpecificationExecutor<BacSi> {

    List<BacSi> findTop8ByOrderBySoNamKinhNghiemDesc();

    List<BacSi> findByIdIn(List<Long> danhSachMa);

    Optional<BacSi> findByTaiKhoanId(Long taiKhoanId);
}