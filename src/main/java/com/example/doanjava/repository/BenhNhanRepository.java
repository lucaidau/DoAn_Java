package vn.carehealth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.carehealth.model.BenhNhan;

public interface BenhNhanRepository extends JpaRepository<BenhNhan, Long> {
    Optional<BenhNhan> findFirstByTaiKhoanId(Long taiKhoanId);
}
