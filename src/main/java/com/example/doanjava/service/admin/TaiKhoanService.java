package com.example.doanjava.service.admin;

import com.example.doanjava.dto.admin.TaiKhoanDTO;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaiKhoanService {
    @Autowired
    TaiKhoanRepository taiKhoanRepository;

    public Page<TaiKhoanDTO> getTaiKhoan(int page, int size, String keyword, String vaiTro, Boolean trangThai)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        // Định nghĩa Specification động dựa trên các tham số truyền vào
        Specification<TaiKhoan> spec = (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction(); // Mệnh đề mặc định (true)

            // 1. Lọc theo từ khóa (Tìm trong tenDangNhap, email, soDienThoai hoặc tên Bác sĩ / Bệnh nhân)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                var joinBacSi = root.join("bacSi", jakarta.persistence.criteria.JoinType.LEFT);
                var joinBenhNhan = root.join("benhNhan", jakarta.persistence.criteria.JoinType.LEFT);

                var keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenDangNhap")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern),
                        criteriaBuilder.like(root.get("soDienThoai"), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(joinBacSi.get("hoTen")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(joinBenhNhan.get("hoTen")), pattern)
                );
                predicates = criteriaBuilder.and(predicates, keywordPredicate);
            }

            // 2. Lọc theo vai trò
            if (vaiTro != null && !vaiTro.trim().isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("vaiTro"), vaiTro));
            }
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.notEqual(criteriaBuilder.upper(root.get("vaiTro")), "ADMIN")
            );

            // 3. Lọc theo trạng thái
            if (trangThai != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return predicates;
        };

        // Gọi trực tiếp findAll với Specification và Pageable
        Page<TaiKhoan> taiKhoans = taiKhoanRepository.findAll(spec, pageable);
        return taiKhoans.map(this::convertToDTO);
    }


    private TaiKhoanDTO convertToDTO(TaiKhoan taiKhoan)
    {
    TaiKhoanDTO dto =  new TaiKhoanDTO();
    dto.setId(taiKhoan.getId());

    dto.setEmail(taiKhoan.getEmail());
    dto.setSoDienThoai(taiKhoan.getSoDienThoai());
    dto.setNgayTao(taiKhoan.getNgayTao());
    dto.setTrangThai(taiKhoan.getTrangThai());

    String vaiTro = taiKhoan.getVaiTro();
    if("BAC_SI".equals(vaiTro) && taiKhoan.getBacSi() != null)
    {
        dto.setHoTen(taiKhoan.getBacSi().getHoTen());
        dto.setVaiTro("Bác sĩ");
    } else if ("BENH_NHAN".equals(vaiTro) && taiKhoan.getBenhNhan() != null)
    {
        dto.setHoTen(taiKhoan.getBenhNhan().getHoTen());
        dto.setVaiTro("Bệnh nhân");
    }
    else
    {
        dto.setHoTen("Quản trị viên (" + taiKhoan.getTenDangNhap() + ")");
        dto.setVaiTro("Admin");
    }
        return dto;
    }

    @Transactional
    public boolean doiTrangThaiTaiKhoan(Long id)
    {
        TaiKhoan tk = taiKhoanRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Không tìm thấy tài khoản"));

        boolean trangThaiMoi = !Boolean.TRUE.equals(tk.getTrangThai());
        tk.setTrangThai(trangThaiMoi);
        taiKhoanRepository.save(tk);
        return trangThaiMoi;
    }
}