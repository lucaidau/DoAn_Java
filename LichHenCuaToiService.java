package com.example.doanjava.service;

import com.example.doanjava.entity.BenhNhan;
import com.example.doanjava.entity.ChuyenKhoa;
import com.example.doanjava.entity.LichHen;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.repository.BenhNhanRepository;
import com.example.doanjava.repository.LichHenCuaToiRepository;
import com.example.doanjava.repository.TaiKhoanRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

@Service
public class LichHenCuaToiService {
    private static final Set<String> TRANG_THAI_HOP_LE =
            Set.of("ALL", "UPCOMING", "CHO_XAC_NHAN", "DA_KHAM", "DA_HUY");
    private static final Set<String> KY_HAN_HOP_LE =
            Set.of("all-time", "this-month", "next-month", "last-month");
    private static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));

    private final LichHenCuaToiRepository lichHenRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final BenhNhanRepository benhNhanRepository;

    public LichHenCuaToiService(
            LichHenCuaToiRepository lichHenRepository,
            TaiKhoanRepository taiKhoanRepository,
            BenhNhanRepository benhNhanRepository) {
        this.lichHenRepository = lichHenRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.benhNhanRepository = benhNhanRepository;
    }

    public DuLieuTrang layDuLieu(Authentication authentication, String search, String status,
                                  Long specialtyId, String period) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Bạn cần đăng nhập để xem lịch hẹn");
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản bệnh nhân"));
        BenhNhan benhNhan = benhNhanRepository.findFirstByTaiKhoanId(taiKhoan.getId())
                .orElseThrow(() -> new IllegalStateException("Tài khoản chưa có hồ sơ bệnh nhân"));

        String trangThaiLoc = status == null || status.isBlank() ? "ALL" : status.toUpperCase(Locale.ROOT);
        String kyHanLoc = period == null || period.isBlank() ? "all-time" : period.toLowerCase(Locale.ROOT);
        if (!TRANG_THAI_HOP_LE.contains(trangThaiLoc)) {
            throw new IllegalArgumentException("Trạng thái lịch hẹn không hợp lệ");
        }
        if (!KY_HAN_HOP_LE.contains(kyHanLoc)) {
            throw new IllegalArgumentException("Khoảng thời gian không hợp lệ");
        }

        List<LichHen> tatCaLichHen =
                lichHenRepository.findByBenhNhan_IdOrderByNgayKhamAscKhungGioKhamAsc(benhNhan.getId());
        List<LichHenHienThi> lichHenHienThi = tatCaLichHen.stream()
                .filter(lichHen -> khopTrangThai(lichHen, trangThaiLoc))
                .filter(lichHen -> specialtyId == null || coChuyenKhoa(lichHen, specialtyId))
                .filter(lichHen -> khopKyHan(lichHen, kyHanLoc))
                .filter(lichHen -> khopTuKhoa(lichHen, search))
                .sorted(this::soSanhLichHen)
                .map(this::chuyenSangHienThi)
                .toList();

        Map<Long, ChuyenKhoaOption> chuyenKhoaTheoId = new TreeMap<>();
        for (LichHen lichHen : tatCaLichHen) {
            ChuyenKhoa chuyenKhoa = lichHen.getBacSi() == null ? null : lichHen.getBacSi().getChuyenKhoa();
            if (chuyenKhoa != null && chuyenKhoa.getId() != null) {
                chuyenKhoaTheoId.putIfAbsent(chuyenKhoa.getId(),
                        new ChuyenKhoaOption(chuyenKhoa.getId(),
                                Objects.toString(chuyenKhoa.getTenChuyenKhoa(), "Chuyên khoa chưa cập nhật")));
            }
        }
        List<ChuyenKhoaOption> chuyenKhoas = new ArrayList<>(chuyenKhoaTheoId.values());
        chuyenKhoas.sort(Comparator.comparing(ChuyenKhoaOption::ten, String.CASE_INSENSITIVE_ORDER));

        LocalDate homNay = LocalDate.now();
        long sapToi = tatCaLichHen.stream()
                .filter(lichHen -> lichHen.getNgayKham() != null && !lichHen.getNgayKham().isBefore(homNay))
                .filter(lichHen -> Set.of("DA_XAC_NHAN", "DANG_KHAM").contains(lichHen.getTrangThai()))
                .count();

        return new DuLieuTrang(
                benhNhan,
                lichHenHienThi,
                chuyenKhoas,
                tatCaLichHen.size(),
                sapToi,
                demTheoTrangThai(tatCaLichHen, "CHO_XAC_NHAN"),
                demTheoTrangThai(tatCaLichHen, "DA_KHAM"),
                demTheoTrangThai(tatCaLichHen, "DA_HUY"));
    }

    private boolean khopTrangThai(LichHen lichHen, String status) {
        return switch (status) {
            case "ALL" -> true;
            case "UPCOMING" -> lichHen.getNgayKham() != null
                    && !lichHen.getNgayKham().isBefore(LocalDate.now())
                    && Set.of("DA_XAC_NHAN", "DANG_KHAM").contains(lichHen.getTrangThai());
            default -> status.equals(lichHen.getTrangThai());
        };
    }

    private boolean khopKyHan(LichHen lichHen, String period) {
        if ("all-time".equals(period)) {
            return true;
        }
        if (lichHen.getNgayKham() == null) {
            return false;
        }
        YearMonth thangHienTai = YearMonth.now();
        YearMonth thangCanTim = switch (period) {
            case "this-month" -> thangHienTai;
            case "next-month" -> thangHienTai.plusMonths(1);
            case "last-month" -> thangHienTai.minusMonths(1);
            default -> throw new IllegalArgumentException("Khoảng thời gian không hợp lệ");
        };
        return YearMonth.from(lichHen.getNgayKham()).equals(thangCanTim);
    }

    private boolean khopTuKhoa(LichHen lichHen, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String tuKhoa = search.trim().toLowerCase(Locale.ROOT);
        String tenBacSi = lichHen.getBacSi() == null ? "" : Objects.toString(lichHen.getBacSi().getHoTen(), "");
        String tenChuyenKhoa = lichHen.getBacSi() == null || lichHen.getBacSi().getChuyenKhoa() == null
                ? "" : Objects.toString(lichHen.getBacSi().getChuyenKhoa().getTenChuyenKhoa(), "");
        String trieuChung = Objects.toString(lichHen.getTrieuChungBenh(), "");
        String maLichHen = "LH-" + lichHen.getId();
        return (tenBacSi + " " + tenChuyenKhoa + " " + trieuChung + " " + maLichHen)
                .toLowerCase(Locale.ROOT).contains(tuKhoa);
    }

    private boolean coChuyenKhoa(LichHen lichHen, Long specialtyId) {
        return lichHen.getBacSi() != null
                && lichHen.getBacSi().getChuyenKhoa() != null
                && specialtyId.equals(lichHen.getBacSi().getChuyenKhoa().getId());
    }

    private int soSanhLichHen(LichHen a, LichHen b) {
        boolean aSapToi = laLichSapToi(a);
        boolean bSapToi = laLichSapToi(b);
        int uuTien = Boolean.compare(bSapToi, aSapToi);
        if (uuTien != 0) {
            return uuTien;
        }
        Comparator<LocalDate> ngayTangDan = Comparator.nullsLast(Comparator.naturalOrder());
        int theoNgay = ngayTangDan.compare(a.getNgayKham(), b.getNgayKham());
        return aSapToi ? theoNgay : -theoNgay;
    }

    private boolean laLichSapToi(LichHen lichHen) {
        return lichHen.getNgayKham() != null
                && !lichHen.getNgayKham().isBefore(LocalDate.now())
                && !"DA_HUY".equals(lichHen.getTrangThai())
                && !"DA_KHAM".equals(lichHen.getTrangThai());
    }

    private LichHenHienThi chuyenSangHienThi(LichHen lichHen) {
        String trangThai = lichHen.getTrangThai();
        String nhanTrangThai = switch (trangThai == null ? "" : trangThai) {
            case "CHO_XAC_NHAN" -> "Chờ xác nhận";
            case "DA_XAC_NHAN" -> "Đã xác nhận";
            case "DANG_KHAM" -> "Đang khám";
            case "DA_KHAM" -> "Đã khám hoàn tất";
            case "DA_HUY" -> "Đã hủy";
            default -> "Chưa cập nhật";
        };
        String mauTrangThai = switch (trangThai == null ? "" : trangThai) {
            case "CHO_XAC_NHAN" -> "bg-status-pending/15 text-status-pending";
            case "DA_KHAM" -> "bg-status-completed/15 text-status-completed";
            case "DA_HUY" -> "bg-status-cancelled/15 text-status-cancelled";
            case "DANG_KHAM" -> "bg-primary-fixed text-on-primary-fixed";
            default -> "bg-status-confirmed/15 text-status-confirmed";
        };
        String tenBacSi = lichHen.getBacSi() == null
                ? "Bác sĩ chưa được phân công"
                : String.join(" ", List.of(
                        Objects.toString(lichHen.getBacSi().getHocHamHocVi(), ""),
                        Objects.toString(lichHen.getBacSi().getHoTen(), "")).stream()
                        .filter(value -> !value.isBlank()).toList());
        String tenChuyenKhoa = lichHen.getBacSi() == null || lichHen.getBacSi().getChuyenKhoa() == null
                ? "Chưa cập nhật chuyên khoa"
                : lichHen.getBacSi().getChuyenKhoa().getTenChuyenKhoa();
        String tenPhong = tenPhong(lichHen);

        return new LichHenHienThi(
                lichHen.getId(),
                "LH-" + lichHen.getId(),
                tenBacSi,
                tenChuyenKhoa,
                lichHen.getNgayKham() == null ? "Chưa xác định" : DINH_DANG_NGAY.format(lichHen.getNgayKham()),
                Objects.toString(lichHen.getKhungGioKham(), "Chưa xác định"),
                tenPhong,
                Objects.toString(lichHen.getTrieuChungBenh(), "Chưa có thông tin"),
                nhanTrangThai,
                mauTrangThai);
    }

    private String tenPhong(LichHen lichHen) {
        if (lichHen.getPhongKham() != null) {
            String ten = Objects.toString(lichHen.getPhongKham().getTenPhong(), "").trim();
            String khuVuc = Objects.toString(lichHen.getPhongKham().getKhuVuc(), "").trim();
            if (!ten.isEmpty() && !khuVuc.isEmpty()) {
                return ten + " • " + khuVuc;
            }
            if (!ten.isEmpty()) {
                return ten;
            }
            if (!khuVuc.isEmpty()) {
                return khuVuc;
            }
        }

        return switch (Objects.toString(lichHen.getTrangThai(), "")) {
            case "CHO_XAC_NHAN" -> "Sẽ được thông báo khi xác nhận";
            case "DA_HUY" -> "Lịch hẹn đã hủy";
            default -> "Chưa cập nhật địa điểm khám";
        };
    }

    private long demTheoTrangThai(List<LichHen> lichHens, String status) {
        return lichHens.stream().filter(lichHen -> status.equals(lichHen.getTrangThai())).count();
    }

    public record ChuyenKhoaOption(Long id, String ten) {
    }

    public record LichHenHienThi(Long id, String maLichHen, String tenBacSi, String tenChuyenKhoa,
                                 String ngayKham, String khungGioKham, String tenPhong,
                                 String trieuChung, String nhanTrangThai, String mauTrangThai) {
    }

    public record DuLieuTrang(BenhNhan benhNhan, List<LichHenHienThi> lichHens,
                              List<ChuyenKhoaOption> chuyenKhoas, long tongTatCa,
                              long sapToi, long choXacNhan, long daKham, long daHuy) {
    }
}
