
package com.example.doanjava.repository;


import com.example.doanjava.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {
    // -- Lấy thanh toán theo mã lịch hẹn --
    Optional<ThanhToan> findByLichHenId(Long lichHenId);
}
