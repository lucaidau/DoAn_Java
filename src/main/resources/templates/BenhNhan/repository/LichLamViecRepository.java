package com.example.baitapluyen.repository;

import com.example.baitapluyen.model.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LichLamViecRepository extends JpaRepository<LichLamViec, Long> {
    List<LichLamViec> findByBacSi_IdAndNgayLamViecGreaterThanEqualOrderByNgayLamViecAscGioBatDauAsc(
            Long bacSiId, LocalDate ngayHienTai);
}
