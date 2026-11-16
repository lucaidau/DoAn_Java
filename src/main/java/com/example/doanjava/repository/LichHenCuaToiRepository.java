package com.example.doanjava.repository;

import com.example.doanjava.entity.LichHen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichHenCuaToiRepository extends JpaRepository<LichHen, Long> {
    List<LichHen> findByBenhNhan_IdOrderByNgayKhamAscKhungGioKhamAsc(Long benhNhanId);
}
