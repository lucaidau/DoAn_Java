package com.example.doanjava.repository;

import com.example.doanjava.entity.LichHen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
public interface LichHenRepository extends JpaRepository<LichHen, Long> {
    //tìm theo mã lịch hẹn, tên bệnh nhân, số điện thoại, trạng thái và ngày khám
    @Query("select l from LichHen l where (:search = '' or lower(l.benhNhan.hoTen) like lower(concat('%', :search, '%')) or str(l.id) like concat('%', :search, '%') or lower(coalesce(l.benhNhan.taiKhoan.soDienThoai, '')) like lower(concat('%', :search, '%'))) and (:status = 'ALL' or l.trangThai = :status) and (:fromDate is null or l.ngayKham >= :fromDate) and (:toDate is null or l.ngayKham <= :toDate) and (:bacSiId is null or l.bacSi.id = :bacSiId)")
    Page<LichHen> timKiem(@Param("search") String search, @Param("status") String status, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("bacSiId") Long bacSiId, Pageable pageable);

    long countByBacSiId(Long bacSiId);
    long countByBacSiIdAndTrangThai(Long bacSiId, String trangThai);
    long countByBacSiIdAndNgayKham(Long bacSiId, LocalDate ngay);
    List<LichHen> findTop10ByBacSiIdOrderByNgayTaoDesc(Long bacSiId);

    long countByNgayKham(LocalDate ngay);

    long countByNgayKhamAndTrangThai(LocalDate ngay, String trangThai);

    long countByTrangThai(String trangThai);

    long countByLichLamViecIdAndTrangThaiNot(Long caId, String trangThai);

    boolean existsByNgayKhamAndKhungGioKhamAndTrangThaiNotAndIdNot(LocalDate ngay, String gio, String trangThai, Long id);

    List<LichHen> findTop10ByOrderByNgayTaoDesc();
}
