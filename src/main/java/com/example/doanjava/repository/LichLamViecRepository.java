package com.example.doanjava.repository;

import com.example.doanjava.entity.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface LichLamViecRepository extends JpaRepository<LichLamViec, Long> {
    List<LichLamViec> findByNgayLamViecBetween(LocalDate tuNgay, LocalDate denNgay);

    // Danh sách ca trong khoảng ngày, bacSiId = null thì lấy tất cả bác sĩ
    @Query("select c from LichLamViec c " +
            "where c.ngayLamViec between :tu and :den " +
            "and (:bacSiId is null or c.bacSi.id = :bacSiId) " +
            "order by c.ngayLamViec asc, c.gioBatDau asc")
    List<LichLamViec> locTheoKhoangNgay(@Param("tu") LocalDate tu,
                                        @Param("den") LocalDate den,
                                        @Param("bacSiId") Long bacSiId);

    // Hai khoảng giờ giao nhau khi: bắt đầu A < kết thúc B và kết thúc A > bắt đầu B
    @Query("select count(c) from LichLamViec c " +
            "where c.bacSi.id = :bacSiId and c.ngayLamViec = :ngay " +
            "and c.gioBatDau < :ketThuc and c.gioKetThuc > :batDau")
    long demTrungBacSi(@Param("bacSiId") Long bacSiId,
                       @Param("ngay") LocalDate ngay,
                       @Param("batDau") LocalTime batDau,
                       @Param("ketThuc") LocalTime ketThuc);

    @Query("select count(c) from LichLamViec c " +
            "where c.phongKham.id = :phongId and c.ngayLamViec = :ngay " +
            "and c.gioBatDau < :ketThuc and c.gioKetThuc > :batDau")
    long demTrungPhong(@Param("phongId") Long phongId,
                       @Param("ngay") LocalDate ngay,
                       @Param("batDau") LocalTime batDau,
                       @Param("ketThuc") LocalTime ketThuc);
}