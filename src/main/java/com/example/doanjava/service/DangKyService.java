package com.example.doanjava.service;

import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.repository.BenhNhanRepository;
import com.example.doanjava.repository.TaiKhoanRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.carehealth.dto.ThongTinDangKy;


@Service
public class DangKyService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final PasswordEncoder passwordEncoder;

    public DangKyService(
            TaiKhoanRepository taiKhoanRepository,
            BenhNhanRepository benhNhanRepository,
            PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void dangKy(ThongTinDangKy thongTin) {
        if (!thongTin.getPassword().equals(thongTin.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }
        if (!"on".equals(thongTin.getTerms())) {
            throw new IllegalArgumentException("Bạn cần đồng ý với điều khoản");
        }

        String phone = thongTin.getPhone().trim();
        String email = thongTin.getEmail().trim().toLowerCase(java.util.Locale.ROOT);

        if (taiKhoanRepository.existsBySoDienThoai(phone)
                || taiKhoanRepository.existsByTenDangNhap(phone)) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
        }
        if (taiKhoanRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(phone);
        taiKhoan.setMatKhau(passwordEncoder.encode(thongTin.getPassword()));
        taiKhoan.setSoDienThoai(phone);
        taiKhoan.setEmail(email);
        taiKhoan.setVaiTro("BENH_NHAN");
        taiKhoan.setTrangThai(true);

        try {
            taiKhoan = taiKhoanRepository.saveAndFlush(taiKhoan);
            BenhNhan benhNhan = new BenhNhan();
            benhNhan.setTaiKhoan(taiKhoan);
            benhNhan.setHoTen(thongTin.getFullName().trim());
            benhNhan.setNgaySinh(thongTin.getBirthDate());
            benhNhan.setGioiTinh(thongTin.getGender());
            benhNhanRepository.saveAndFlush(benhNhan);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại hoặc dữ liệu không hợp lệ", exception);
        }
    }
}
