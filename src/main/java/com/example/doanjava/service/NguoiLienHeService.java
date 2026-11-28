package com.example.doanjava.service;

import com.example.doanjava.dto.NguoiLienHeDTO;
import com.example.doanjava.dto.admin.TaiKhoanDTO;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.repository.LichHenRepository;
import com.example.doanjava.repository.TaiKhoanRepository;
import com.example.doanjava.service.admin.TaiKhoanService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Trả lời câu hỏi: "Người dùng này được bắt đầu nhắn tin với những ai?"
 *  - Admin: tìm được mọi bác sĩ / bệnh nhân đang hoạt động.
 *  - Bệnh nhân: các bác sĩ đã có lịch hẹn chưa hủy với mình, cộng với admin (hỗ trợ).
 *  - Bác sĩ: các bệnh nhân đã có lịch hẹn chưa hủy với mình, cộng với admin (hỗ trợ).
 * Danh sách này khớp với quy tắc trong TinNhanService.duocNhanTin.
 */
@Service
public class NguoiLienHeService {

    private static final int SO_KET_QUA_ADMIN_TOI_DA = 20;

    private static final String VAI_TRO_ADMIN = "ADMIN";
    private static final String VAI_TRO_BAC_SI = "BAC_SI";
    private static final String VAI_TRO_BENH_NHAN = "BENH_NHAN";

    private final TaiKhoanRepository taiKhoanRepository;
    private final LichHenRepository lichHenRepository;
    private final TaiKhoanService taiKhoanService;

    public NguoiLienHeService(TaiKhoanRepository taiKhoanRepository,
                              LichHenRepository lichHenRepository,
                              TaiKhoanService taiKhoanService) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.lichHenRepository = lichHenRepository;
        this.taiKhoanService = taiKhoanService;
    }

    /** Từ tên đăng nhập (hoặc email) của người đang đăng nhập, lấy ra id tài khoản. */
    public Long layIdTheoDangNhap(String dangNhap) {
        return taiKhoanRepository.findByTenDangNhapIgnoreCase(dangNhap)
                .or(() -> taiKhoanRepository.findByEmailIgnoreCase(dangNhap))
                .map(TaiKhoan::getId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập!"));
    }

    @Transactional(readOnly = true)
    public List<NguoiLienHeDTO> layDanhSach(Long idNguoiDung, String tuKhoaNhap) {
        TaiKhoan nguoiDung = taiKhoanRepository.findById(idNguoiDung)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản của bạn!"));

        String vaiTro = layVaiTro(nguoiDung);
        String tuKhoa = (tuKhoaNhap == null) ? "" : tuKhoaNhap.trim();

        // ----- Trường hợp 1: admin -----
        if (vaiTro.equals(VAI_TRO_ADMIN)) {
            return layChoAdmin(tuKhoa);
        }

        // ----- Trường hợp 2: bệnh nhân hoặc bác sĩ -----
        List<TaiKhoan> danhSachUngVien = new ArrayList<>();

        if (vaiTro.equals(VAI_TRO_BENH_NHAN) && nguoiDung.getBenhNhan() != null) {
            // Bệnh nhân: các bác sĩ đã có lịch hẹn với mình
            List<Long> danhSachIdBacSi = lichHenRepository.timIdBacSiCoLichHen(nguoiDung.getBenhNhan().getId(), "DA_HUY");
            if (!danhSachIdBacSi.isEmpty()) {
                danhSachUngVien.addAll(taiKhoanRepository.findByBacSi_IdIn(danhSachIdBacSi));
            }
        }
        if (vaiTro.equals(VAI_TRO_BAC_SI) && nguoiDung.getBacSi() != null) {
            // Bác sĩ: các bệnh nhân đã có lịch hẹn với mình
            List<Long> danhSachIdBenhNhan = lichHenRepository.timIdBenhNhanCoLichHen(nguoiDung.getBacSi().getId(),"DA_HUY");
            if (!danhSachIdBenhNhan.isEmpty()) {
                danhSachUngVien.addAll(taiKhoanRepository.findByBenhNhan_IdIn(danhSachIdBenhNhan));
            }
        }
        // Ai cũng liên hệ được quản trị viên để nhờ hỗ trợ
        danhSachUngVien.addAll(taiKhoanRepository.findByVaiTroIgnoreCase(VAI_TRO_ADMIN));

        // Lọc: bỏ chính mình, bỏ tài khoản bị khóa, chỉ giữ tên khớp từ khóa
        List<NguoiLienHeDTO> ketQua = new ArrayList<>();
        String tuKhoaThuong = tuKhoa.toLowerCase();
        for (TaiKhoan ungVien : danhSachUngVien) {
            boolean laChinhMinh = ungVien.getId().equals(idNguoiDung);
            boolean dangHoatDong = Boolean.TRUE.equals(ungVien.getTrangThai());
            if (laChinhMinh || !dangHoatDong) {
                continue;
            }

            String ten = layTenHienThi(ungVien);
            if (!ten.toLowerCase().contains(tuKhoaThuong)) {
                continue;
            }
            ketQua.add(new NguoiLienHeDTO(ungVien.getId(), ten, layNhanVaiTro(ungVien)));
        }

        ketQua.sort(Comparator.comparing(NguoiLienHeDTO::getTen, String.CASE_INSENSITIVE_ORDER));
        return ketQua;
    }

    // Admin dùng lại bộ tìm kiếm tài khoản sẵn có (đã loại admin, đã tìm theo tên / email / số điện thoại)
    private List<NguoiLienHeDTO> layChoAdmin(String tuKhoa) {
        List<NguoiLienHeDTO> ketQua = new ArrayList<>();
        Page<TaiKhoanDTO> trang = taiKhoanService.getTaiKhoan(0, SO_KET_QUA_ADMIN_TOI_DA, tuKhoa, null, true);
        for (TaiKhoanDTO taiKhoan : trang.getContent()) {
            ketQua.add(new NguoiLienHeDTO(taiKhoan.getId(), taiKhoan.getHoTen(), taiKhoan.getVaiTro()));
        }
        return ketQua;
    }

    private String layVaiTro(TaiKhoan taiKhoan) {
        if (taiKhoan.getVaiTro() == null) {
            return "";
        }
        return taiKhoan.getVaiTro().trim().toUpperCase();
    }

    private String layNhanVaiTro(TaiKhoan taiKhoan) {
        String vaiTro = layVaiTro(taiKhoan);
        if (vaiTro.equals(VAI_TRO_BAC_SI)) {
            return "Bác sĩ";
        }
        if (vaiTro.equals(VAI_TRO_BENH_NHAN)) {
            return "Bệnh nhân";
        }
        return "Admin";
    }

    private String layTenHienThi(TaiKhoan taiKhoan) {
        String vaiTro = layVaiTro(taiKhoan);
        if (vaiTro.equals(VAI_TRO_BAC_SI) && taiKhoan.getBacSi() != null) {
            return taiKhoan.getBacSi().getHoTen();
        }
        if (vaiTro.equals(VAI_TRO_BENH_NHAN) && taiKhoan.getBenhNhan() != null) {
            return taiKhoan.getBenhNhan().getHoTen();
        }
        return "Quản trị viên (" + taiKhoan.getTenDangNhap() + ")";
    }
}