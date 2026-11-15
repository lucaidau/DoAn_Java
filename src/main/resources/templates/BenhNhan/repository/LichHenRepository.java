package com.example.baitapluyen.repository;

import com.example.baitapluyen.model.LichHen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LichHenRepository extends JpaRepository<LichHen, Long> {
    List<LichHen> findByLichLamViec_IdAndTrangThaiNot(Long lichLamViecId, String trangThai);
    long countByLichLamViec_IdAndTrangThaiNot(Long lichLamViecId, String trangThai);
    Optional<LichHen> findByIdAndBenhNhan_Id(Long id, Long benhNhanId);
}
