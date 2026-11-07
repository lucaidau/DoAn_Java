package vn.carehealth.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.carehealth.model.TaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long> {
    Optional<TaiKhoan> findByTenDangNhapIgnoreCase(String tenDangNhap);
    Optional<TaiKhoan> findByEmailIgnoreCase(String email);
    List<TaiKhoan> findBySoDienThoai(String soDienThoai);
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsBySoDienThoai(String soDienThoai);
}
