
package com.example.doanjava.service;

import com.example.doanjava.dto.CapNhatHoSoBenhNhan;

import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.entity.TaiKhoan;

import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.BenhNhanRepository;
import com.example.doanjava.repository.TaiKhoanRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class HoSoService {

    private final TaiKhoanRepository taiKhoanRepository;

    private final BenhNhanRepository benhNhanRepository;

    private final BacSiRepository bacSiRepository;

    public HoSoService(
            TaiKhoanRepository taiKhoanRepository,
            BenhNhanRepository benhNhanRepository,
            BacSiRepository bacSiRepository
    ) {

        this.taiKhoanRepository = taiKhoanRepository;

        this.benhNhanRepository = benhNhanRepository;

        this.bacSiRepository = bacSiRepository;
    }

    // LẤY THÔNG TIN TÀI KHOẢN ĐANG ĐĂNG NHẬP

    @Transactional(readOnly = true)
    public TaiKhoan layTaiKhoan(
            Authentication authentication
    ) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Bạn chưa đăng nhập"
            );
        }

        return taiKhoanRepository
                .findByTenDangNhapIgnoreCase(
                        authentication.getName()
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Tài khoản không tồn tại"
                        )
                );
    }

    // LẤY HỒ SƠ BỆNH NHÂN

    @Transactional(readOnly = true)
    public BenhNhan layBenhNhan(
            TaiKhoan taiKhoan
    ) {

        return benhNhanRepository
                .findFirstByTaiKhoanId(
                        taiKhoan.getId()
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Chưa có hồ sơ bệnh nhân"
                        )
                );
    }

    // LẤY HỒ SƠ BÁC SĨ

    @Transactional(readOnly = true)
    public BacSi layBacSi(
            TaiKhoan taiKhoan
    ) {

        return bacSiRepository
                .findByTaiKhoanId(
                        taiKhoan.getId()
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Chưa có hồ sơ bác sĩ"
                        )
                );
    }

    // CẬP NHẬT HỒ SƠ BỆNH NHÂN

    @Transactional
    public void capNhatHoSoBenhNhan(
            Authentication authentication,
            CapNhatHoSoBenhNhan duLieu
    ) {

        TaiKhoan taiKhoan = layTaiKhoan(
                authentication
        );

        if (!"BENH_NHAN".equals(taiKhoan.getVaiTro())) {

            throw new IllegalArgumentException(
                    "Tài khoản không có quyền cập nhật hồ sơ bệnh nhân"
            );
        }

        BenhNhan benhNhan = layBenhNhan(
                taiKhoan
        );

        if (duLieu.getHoTen() == null
                || duLieu.getHoTen().isBlank()) {

            throw new IllegalArgumentException(
                    "Họ tên không được để trống"
            );
        }

        if (duLieu.getEmail() == null
                || duLieu.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Email không được để trống"
            );
        }

        if (duLieu.getSoDienThoai() == null
                || duLieu.getSoDienThoai().isBlank()) {

            throw new IllegalArgumentException(
                    "Số điện thoại không được để trống"
            );
        }

        String email = duLieu.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String soDienThoai = duLieu.getSoDienThoai()
                .trim();

        boolean emailDaTonTai = taiKhoanRepository
                .findByEmailIgnoreCase(email)
                .filter(
                        tk -> !tk.getId().equals(
                                taiKhoan.getId()
                        )
                )
                .isPresent();

        if (emailDaTonTai) {

            throw new IllegalArgumentException(
                    "Email đã được tài khoản khác sử dụng"
            );
        }

        boolean soDienThoaiDaTonTai = taiKhoanRepository
                .findBySoDienThoai(soDienThoai)
                .stream()
                .anyMatch(
                        tk -> !tk.getId().equals(
                                taiKhoan.getId()
                        )
                );

        if (soDienThoaiDaTonTai) {

            throw new IllegalArgumentException(
                    "Số điện thoại đã được tài khoản khác sử dụng"
            );
        }

        taiKhoan.setEmail(email);

        taiKhoan.setSoDienThoai(soDienThoai);

        benhNhan.setHoTen(
                duLieu.getHoTen().trim()
        );

        benhNhan.setNgaySinh(
                duLieu.getNgaySinh()
        );

        benhNhan.setGioiTinh(
                duLieu.getGioiTinh()
        );

        String nhomMau = duLieu.getNhomMau();

        if (nhomMau != null && !nhomMau.isBlank()) {

            nhomMau = nhomMau.trim().toUpperCase(Locale.ROOT);

            if (!java.util.Set.of(
                    "A+", "A-",
                    "B+", "B-",
                    "AB+", "AB-",
                    "O+", "O-"
            ).contains(nhomMau)) {

                throw new IllegalArgumentException(
                        "Nhóm máu không hợp lệ"
                );
            }

        } else {

            nhomMau = null;
        }

        benhNhan.setNhomMau(nhomMau);

        benhNhan.setDiaChi(
                duLieu.getDiaChi() == null
                        ? ""
                        : duLieu.getDiaChi().trim()
        );

        benhNhan.setTienSuBenh(
                duLieu.getTienSuBenh() == null
                        ? ""
                        : duLieu.getTienSuBenh().trim()
        );

        taiKhoanRepository.save(taiKhoan);

        benhNhanRepository.save(benhNhan);
    }

    // CẬP NHẬT HỒ SƠ BÁC SĨ

    @Transactional
    public void capNhatHoSoBacSi(
            Authentication authentication,
            String hoTen,
            String hocHamHocVi,
            Integer soNamKinhNghiem,
            String gioiThieu
    ) {

        TaiKhoan taiKhoan = layTaiKhoan(
                authentication
        );

        if (!"BAC_SI".equals(taiKhoan.getVaiTro())) {

            throw new IllegalArgumentException(
                    "Chỉ bác sĩ mới được cập nhật hồ sơ này"
            );
        }

        BacSi bacSi = layBacSi(
                taiKhoan
        );

        if (hoTen == null || hoTen.isBlank()) {

            throw new IllegalArgumentException(
                    "Họ tên bác sĩ không được để trống"
            );
        }

        if (soNamKinhNghiem == null
                || soNamKinhNghiem < 0
                || soNamKinhNghiem > 80) {

            throw new IllegalArgumentException(
                    "Số năm kinh nghiệm không hợp lệ"
            );
        }

        bacSi.setHoTen(
                hoTen.trim()
        );

        bacSi.setHocHamHocVi(
                hocHamHocVi == null
                        ? ""
                        : hocHamHocVi.trim()
        );

        bacSi.setSoNamKinhNghiem(
                soNamKinhNghiem
        );

        bacSi.setGioiThieu(
                gioiThieu == null
                        ? ""
                        : gioiThieu.trim()
        );

        bacSiRepository.saveAndFlush(bacSi);
    }
}
