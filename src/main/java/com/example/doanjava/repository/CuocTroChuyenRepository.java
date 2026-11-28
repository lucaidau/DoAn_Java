package com.example.doanjava.repository;

import com.example.doanjava.entity.CuocTroChuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CuocTroChuyenRepository extends JpaRepository<CuocTroChuyen, Long> {
    /**
     * Tìm cuộc trò chuyện giữa hai tài khoản.
     * Lưu ý: idNhoHon phải nhỏ hơn idLonHon (đúng quy ước lưu trong DB).
     */
    @Query("select c from CuocTroChuyen c " +
            "where c.taiKhoanNhoHon.id = :idNhoHon and c.taiKhoanLonHon.id = :idLonHon")
    Optional<CuocTroChuyen> timTheoHaiTaiKhoan(@Param("idNhoHon") Long idNhoHon,
                                               @Param("idLonHon") Long idLonHon);

    /** Tất cả cuộc trò chuyện mà một tài khoản tham gia (dù là người nhỏ hơn hay lớn hơn). */
    @Query("select c from CuocTroChuyen c " +
            "where c.taiKhoanNhoHon.id = :idNguoiDung or c.taiKhoanLonHon.id = :idNguoiDung")
    List<CuocTroChuyen> timTheoNguoiDung(@Param("idNguoiDung") Long idNguoiDung);
}
