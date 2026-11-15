package com.example.baitapluyen.repository;

import com.example.baitapluyen.model.ChuyenKhoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChuyenKhoaRepository extends JpaRepository<ChuyenKhoa, Long> {
}
