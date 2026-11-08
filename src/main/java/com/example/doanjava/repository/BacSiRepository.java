package vn.carehealth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.carehealth.model.BacSi;

public interface BacSiRepository extends JpaRepository<BacSi, Long> {
    Optional<BacSi> findByTaiKhoanId(Long taiKhoanId);
}
