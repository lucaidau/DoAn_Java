
package com.example.demo.repository;

import com.example.demo.entity.SinhHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SinhHieuRepository extends JpaRepository<SinhHieu, Long> {
    // -- Lấy sinh hiệu theo mã lịch hẹn --
    Optional<SinhHieu> findByLichHenId(Long lichHenId);
}
