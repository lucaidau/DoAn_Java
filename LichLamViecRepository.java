package com.example.doanjava.repository;
import com.example.doanjava.entity.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.*;
public interface LichLamViecRepository extends JpaRepository<LichLamViec, Long> {
    List<LichLamViec> findByNgayLamViec(LocalDate ngayLamViec);
    List<LichLamViec> findByBacSi_IdAndNgayLamViec(Long maBacSi, LocalDate ngayLamViec);

    // Lich lam viec trong khoang ngay (bao gom ca hai dau)
    List<LichLamViec> findByNgayLamViecBetween(LocalDate tuNgay, LocalDate denNgay);
}
