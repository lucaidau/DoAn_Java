package com.example.demo.repository;
import com.example.demo.entity.HoSoBenhAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
public interface HoSoBenhAnRepository extends JpaRepository<HoSoBenhAn, Long> {
    Optional<HoSoBenhAn> findByLichHenId(Long id);
    @Query("""
    SELECT h
    FROM HoSoBenhAn h
    JOIN h.lichHen l
    WHERE l.benhNhan.id = :benhNhanId
      AND l.id <> :lichHenId
      AND l.trangThai = 'DA_KHAM'
    ORDER BY l.ngayKham DESC, l.id DESC
    """)
    List<HoSoBenhAn> lichSuKham(
            @Param("benhNhanId") Long benhNhanId,
            @Param("lichHenId") Long lichHenId
    );
}
