package com.example.doanjava.service;

import java.util.List;

import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.repository.TaiKhoanRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class TaiKhoanDangNhapService implements UserDetailsService {
    private final TaiKhoanRepository taiKhoanRepository;

    public TaiKhoanDangNhapService(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String tenNhap) throws UsernameNotFoundException {
        String input = tenNhap.trim();
        TaiKhoan taiKhoan;
        if (input.contains("@")) {
            taiKhoan = taiKhoanRepository.findByEmailIgnoreCase(input)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
        } else {
            List<TaiKhoan> matches = taiKhoanRepository.findBySoDienThoai(input);
            if (matches.size() > 1) {
                throw new UsernameNotFoundException("Trùng số điện thoại trong dữ liệu cũ");
            }
            if (matches.size() == 1) {
                taiKhoan = matches.get(0);
            } else {
                taiKhoan = taiKhoanRepository.findByTenDangNhapIgnoreCase(input)
                        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
            }
        }

        return User.withUsername(taiKhoan.getTenDangNhap())
                .password(taiKhoan.getMatKhau())
                .roles(taiKhoan.getVaiTro())
                .disabled(!Boolean.TRUE.equals(taiKhoan.getTrangThai()))
                .build();
    }
}
