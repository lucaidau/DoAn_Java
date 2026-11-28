package com.example.doanjava.repository;

import com.example.doanjava.entity.TinNhan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TinNhanRepository extends JpaRepository<TinNhan, Long> {
    // Trang 0 = các tin mới nhất (sắp giảm dần). Service sẽ đảo lại để hiển thị từ cũ đến mới.
    Page<TinNhan> findByCuocTroChuyenIdOrderByThoiGianGuiDescIdDesc(Long idTroChuyen, Pageable trang);

    // Tin nhắn cuối cùng của một cuộc trò chuyện
    Optional<TinNhan> findFirstByCuocTroChuyenIdOrderByThoiGianGuiDescIdDesc(Long idTroChuyen);

    // Số tin chưa đọc trong một cuộc trò chuyện (chỉ tính tin do ĐỐI PHƯƠNG gửi)
    @Query("select count(t) from TinNhan t " +
            "where t.cuocTroChuyen.id = :idTroChuyen " +
            "and t.nguoiGui.id <> :idNguoiDung " +
            "and t.daDoc = false")
    long demChuaDoc(@Param("idTroChuyen") Long idTroChuyen, @Param("idNguoiDung") Long idNguoiDung);

    // Tổng số tin chưa đọc của một người, qua mọi cuộc trò chuyện (dùng cho huy hiệu trên menu)
    @Query("select count(t) from TinNhan t " +
            "where t.daDoc = false " +
            "and t.nguoiGui.id <> :idNguoiDung " +
            "and (t.cuocTroChuyen.taiKhoanNhoHon.id = :idNguoiDung " +
            "     or t.cuocTroChuyen.taiKhoanLonHon.id = :idNguoiDung)")
    long demChuaDocCuaNguoiDung(@Param("idNguoiDung") Long idNguoiDung);

    // Đánh dấu "đã đọc" cho các tin do đối phương gửi trong một cuộc trò chuyện
    @Modifying
    @Query("update TinNhan t set t.daDoc = true " +
            "where t.cuocTroChuyen.id = :idTroChuyen " +
            "and t.nguoiGui.id <> :idNguoiDung " +
            "and t.daDoc = false")
    int danhDauDaDoc(@Param("idTroChuyen") Long idTroChuyen, @Param("idNguoiDung") Long idNguoiDung);
}
