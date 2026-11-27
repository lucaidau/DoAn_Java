
package com.example.doanjava.service;
import com.example.doanjava.entity.*;
import com.example.doanjava.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
@Service
public class LichHenService {
    public enum KetQuaDoiTrangThai {
        CAP_NHAT,
        TU_DONG_HUY_QUA_HAN
    }

    private final LichHenRepository lichHenRepo;
    @org.springframework.beans.factory.annotation.Autowired private PhongKhamRepository phongKhamRepo;

    @org.springframework.beans.factory.annotation.Autowired
    private SinhHieuRepository sinhHieuRepo;
    @org.springframework.beans.factory.annotation.Autowired

    private final BenhNhanRepository benhNhanRepo;
    private final BacSiRepository bacSiRepo;
    private final LichLamViecRepository caRepo;
    private final HoSoBenhAnRepository hoSoRepo;
    private final DonThuocRepository donThuocRepo;
    public LichHenService(LichHenRepository lichHenRepo, BenhNhanRepository benhNhanRepo, BacSiRepository bacSiRepo, LichLamViecRepository caRepo, HoSoBenhAnRepository hoSoRepo, DonThuocRepository donThuocRepo) {
        this.lichHenRepo = lichHenRepo;
        this.benhNhanRepo = benhNhanRepo;
        this.bacSiRepo = bacSiRepo;
        this.caRepo = caRepo;
        this.hoSoRepo = hoSoRepo;
        this.donThuocRepo = donThuocRepo;
    }

    @org.springframework.beans.factory.annotation.Autowired
    private TaiKhoanRepository taiKhoanRepo;

    // Lấy họ tên bác sĩ đang đăng nhập để hiển thị trên UI
    public String tenBacSiHienTai() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) return "Hệ thống";
            boolean admin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (admin) return "Quản trị viên";
            TaiKhoan tk = taiKhoanRepo.findByTenDangNhapIgnoreCase(auth.getName()).orElse(null);
            if (tk == null) return auth.getName();
            return bacSiRepo.findByTaiKhoanId(tk.getId()).map(BacSi::getHoTen).orElse(auth.getName());
        } catch (Exception e) {
            return "Bác sĩ";
        }
    }

    // ADMIN xem tất cả; BAC_SI chỉ xem lịch hẹn được phân công.
    // Không dùng bacSiId lấy từ URL/form để quyết định phạm vi dữ liệu.
    public Long bacSiIdHienTai() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Chưa đăng nhập");
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (admin) return null;
        boolean bacSi = auth.getAuthorities().stream().anyMatch(a -> "ROLE_BAC_SI".equals(a.getAuthority()));
        if (!bacSi) throw new AccessDeniedException("Không có quyền quản lý lịch hẹn");
        TaiKhoan tk = taiKhoanRepo.findByTenDangNhapIgnoreCase(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản"));
        return bacSiRepo.findByTaiKhoanId(tk.getId())
                .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết hồ sơ bác sĩ"))
                .getId();
    }

    public long tongTatCa() {
        Long id = bacSiIdHienTai();
        return id == null ? lichHenRepo.count() : lichHenRepo.countByBacSiId(id);
    }

    public Page<LichHen> danhSach(String search, String status, LocalDate from, LocalDate to, int page) {
        return lichHenRepo.timKiem(search == null ? "" : search.trim(), status == null || status.isBlank() ? "ALL" : status, from, to, bacSiIdHienTai(), PageRequest.of(Math.max(0, page - 1), 8, Sort.by("ngayKham").descending().and(Sort.by("id").descending())));
    }

    public LichHen chiTiet(Long id) {
        LichHen lich = lichHenRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn " + id));
        Long bacSiId = bacSiIdHienTai();
        if (bacSiId != null && (lich.getBacSi() == null || !bacSiId.equals(lich.getBacSi().getId())))
            throw new AccessDeniedException("Không có quyền truy cập lịch hẹn của bác sĩ khác");
        return lich;
    }

    // Lấy chi tiết lịch hẹn để xem lịch sử khám bệnh (cho phép xem nếu bệnh nhân đó từng khám hoặc đang khám với bác sĩ hiện tại)
    public LichHen chiTietXemLichSu(Long id, Long benhNhanHienTaiId) {
        LichHen lich = lichHenRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn " + id));
        Long bacSiId = bacSiIdHienTai();
        if (bacSiId != null) {
            boolean laCuaMinh = lich.getBacSi() != null && bacSiId.equals(lich.getBacSi().getId());
            boolean cungBenhNhan = benhNhanHienTaiId != null && lich.getBenhNhan() != null && benhNhanHienTaiId.equals(lich.getBenhNhan().getId());
            if (!laCuaMinh && !cungBenhNhan) {
                throw new AccessDeniedException("Không có quyền truy cập lịch hẹn này");
            }
        }
        return lich;
    }

    // Lấy lịch sử khám theo bệnh nhân id, loại trừ lịch hẹn hiện tại
    public List<HoSoBenhAn> lichSuKhamTheoBenhNhan(Long benhNhanId, Long lichIdHienTai) {
        if (benhNhanId == null) return Collections.emptyList();
        return hoSoRepo.lichSuKham(benhNhanId, lichIdHienTai != null ? lichIdHienTai : -1L);
    }

    public List<BenhNhan> benhNhans() {
        return benhNhanRepo.findAll(Sort.by("hoTen"));
    }

    public List<BacSi> bacSis() {
        return bacSiRepo.findAll(Sort.by("hoTen"));
    }

    public List<LichLamViec> caKhams() {
        return caRepo.findAll(Sort.by("ngayLamViec").descending());
    }

    // -- Lọc ca khám theo bác sĩ tại tầng Service --
    public List<LichLamViec> caKhamsTheoBacSi(Long bacSiId) {
        if (bacSiId == null) return List.of();
        return caKhams().stream().filter(ca -> ca.getBacSi() != null && Objects.equals(ca.getBacSi().getId(), bacSiId)).toList();
    }

    // -- Đếm tất cả lịch hẹn theo trạng thái để đồng bộ số liệu trên các tab --
    public long thongKe(String status) {
        Long id = bacSiIdHienTai();
        return id == null ? lichHenRepo.countByTrangThai(status) : lichHenRepo.countByBacSiIdAndTrangThai(id, status);
    }

    // -- Thống kê riêng số lịch có ngày khám là hôm nay --
    public long tongHomNay() {
        Long id = bacSiIdHienTai();
        return id == null ? lichHenRepo.countByNgayKham(LocalDate.now()) : lichHenRepo.countByBacSiIdAndNgayKham(id, LocalDate.now());
    }

    public List<LichHen> thongBao(Long lichHenBiHuyQuaHanId) {
        Long bacSiId = bacSiIdHienTai();
        List<LichHen> lichHens = new ArrayList<>(bacSiId == null
                ? lichHenRepo.findTop10ByOrderByNgayTaoDesc()
                : lichHenRepo.findTop10ByBacSiIdOrderByNgayTaoDesc(bacSiId));
        if (lichHenBiHuyQuaHanId == null) return lichHens;

        LichHen lich = chiTiet(lichHenBiHuyQuaHanId);
        if ("DA_HUY".equals(lich.getTrangThai())
                && lichHens.stream().noneMatch(item -> lichHenBiHuyQuaHanId.equals(item.getId()))) {
            lichHens.add(0, lich);
        }
        return lichHens;
    }

    public List<PhongKham> phongKhams() { return phongKhamRepo.findAll(Sort.by("tenPhong")); }

    public List<DonThuoc> thuocs() {
        List<DonThuoc> list = donThuocRepo.findAll(Sort.by("tenThuoc"));
        Map<String, DonThuoc> unique = new LinkedHashMap<>();
        for (DonThuoc d : list) {
            if (d.getTenThuoc() != null && !d.getTenThuoc().isBlank()) {
                unique.putIfAbsent(d.getTenThuoc().trim(), d);
            }
        }
        return new ArrayList<>(unique.values());
    }

    @Transactional
    public LichHen tao(Long benhNhanId, Long bacSiId, Long caId, LocalDate ngay, String gio, String trieuChung) {
        Long bacSiDangNhap = bacSiIdHienTai();
        if (bacSiDangNhap != null && !bacSiDangNhap.equals(bacSiId))
            throw new AccessDeniedException("Chỉ được tạo lịch cho bác sĩ đang đăng nhập");
        BenhNhan benhNhan = benhNhanRepo.findById(benhNhanId).orElseThrow(() -> new IllegalArgumentException("Bệnh nhân không tồn tại"));
        BacSi bacSi = bacSiRepo.findById(bacSiId).orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại"));
        LichLamViec ca = caRepo.findById(caId).orElseThrow(() -> new IllegalArgumentException("Ca khám không tồn tại"));
        if (!ca.getBacSi().getId().equals(bacSiId) || !ca.getNgayLamViec().equals(ngay)) throw new IllegalArgumentException("Ca khám không thuộc bác sĩ hoặc ngày đã chọn");
        if (gio == null || !gio.matches("\\d{2}:\\d{2} - \\d{2}:\\d{2}")) throw new IllegalArgumentException("Khung giờ phải có dạng 08:00 - 08:30");
        LocalTime batDau = LocalTime.parse(gio.substring(0, 5));
        LocalTime ketThuc = LocalTime.parse(gio.substring(8, 13));
        if (batDau.isBefore(ca.getGioBatDau()) || ketThuc.isAfter(ca.getGioKetThuc()) || !batDau.isBefore(ketThuc)) throw new IllegalArgumentException("Giờ khám nằm ngoài ca làm việc");
        long daDat = lichHenRepo.countByLichLamViecIdAndTrangThaiNot(caId, "DA_HUY");
        if (daDat >= ca.getSoLuongToida()) throw new IllegalArgumentException("Ca khám đã đủ số lượng bệnh nhân");
        LichHen lich = new LichHen();
        lich.setBenhNhan(benhNhan);
        lich.setBacSi(bacSi);
        lich.setLichLamViec(ca);
        lich.setNgayKham(ngay);
        lich.setKhungGioKham(gio);
        lich.setTrieuChungBenh(trieuChung);
        lich.setTrangThai("CHO_XAC_NHAN");
        lich.setNgayTao(LocalDateTime.now());
        return lichHenRepo.save(lich);
    }

    @Transactional
    public LichHen taoDayDu(Long benhNhanId, Long bacSiId, Long caId, LocalDate ngay, String gio, String trieuChung, Long phongId, String ghiChu, Integer tamThu, Integer tamTruong, Integer nhipTim, Integer spo2, java.math.BigDecimal canNang, java.math.BigDecimal chieuCao, String tienSu, String giaDinh, String thoiQuen) {
        if (phongId == null) throw new IllegalArgumentException("Phải chọn phòng khám");
        PhongKham phong = phongKhamRepo.findById(phongId).orElseThrow(() -> new IllegalArgumentException("Phòng khám không tồn tại"));
        if (spo2 != null && (spo2 < 0 || spo2 > 100)) throw new IllegalArgumentException("SpO2 phải từ 0 đến 100");
        if (canNang != null && canNang.signum() <= 0 || chieuCao != null && chieuCao.signum() <= 0) throw new IllegalArgumentException("Cân nặng và chiều cao phải lớn hơn 0");
        LichHen lich = tao(benhNhanId, bacSiId, caId, ngay, gio, trieuChung);
        lich.setPhongKham(phong);
        lich.setGhiChuTiepNhan(ghiChu);
        lichHenRepo.save(lich);
        BenhNhan benhNhan = lich.getBenhNhan();
        if (tienSu != null && !tienSu.isBlank()) benhNhan.setTienSuBenh(tienSu);
        if (giaDinh != null && !giaDinh.isBlank()) benhNhan.setTienSuGiaDinh(giaDinh);
        if (thoiQuen != null && !thoiQuen.isBlank()) benhNhan.setThoiQuenSinhHoat(thoiQuen);
        benhNhanRepo.save(benhNhan);
        if (tamThu != null || tamTruong != null || nhipTim != null || spo2 != null || canNang != null || chieuCao != null) {
            SinhHieu sh = new SinhHieu();
            sh.setLichHen(lich);
            sh.setHuyetApTamThu(tamThu);
            sh.setHuyetApTamTruong(tamTruong);
            sh.setNhipTim(nhipTim);
            sh.setSpo2(spo2);
            sh.setCanNang(canNang);
            sh.setChieuCao(chieuCao);
            sh.setThoiGianDo(LocalDateTime.now());
            sinhHieuRepo.save(sh);
        }
        return lich;
    }

    @Transactional
    public KetQuaDoiTrangThai doiTrangThai(Long id, String hanhDong) {
        LichHen lich = chiTiet(id);
        String hienTai = lich.getTrangThai();
        if ("duyet".equals(hanhDong) && "CHO_XAC_NHAN".equals(hienTai) && daQuaKhungGio(lich)) {
            lich.setTrangThai("DA_HUY");
            lichHenRepo.save(lich);
            return KetQuaDoiTrangThai.TU_DONG_HUY_QUA_HAN;
        }

        String moi = switch (hanhDong) {
            case "duyet" -> "CHO_XAC_NHAN".equals(hienTai) ? "DA_XAC_NHAN" : null;
            case "tuchoi" -> "CHO_XAC_NHAN".equals(hienTai) ? "DA_HUY" : null;
            case "batdaukham" -> "DA_XAC_NHAN".equals(hienTai) ? "DANG_KHAM" : null;
            default -> null;
        };
        if (moi == null) throw new IllegalArgumentException("Không thể thực hiện thao tác với trạng thái hiện tại");
        lich.setTrangThai(moi);
        lichHenRepo.save(lich);
        return KetQuaDoiTrangThai.CAP_NHAT;
    }

    private boolean daQuaKhungGio(LichHen lich) {
        String khungGio = lich.getKhungGioKham();
        if (lich.getNgayKham() == null || khungGio == null || !khungGio.matches("\\d{2}:\\d{2} - \\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Không thể xác định ngày hoặc khung giờ khám để duyệt lịch");
        }

        try {
            String gioKetThuc = khungGio.substring(8);
            LocalDateTime ketThuc = LocalDateTime.of(lich.getNgayKham(), LocalTime.parse(gioKetThuc));
            return !LocalDateTime.now().isBefore(ketThuc);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Khung giờ khám không hợp lệ");
        }
    }

    @Transactional
    public void doiGio(Long id, String gio) {
        LichHen lich = chiTiet(id);
        if (!List.of("CHO_XAC_NHAN", "DA_XAC_NHAN").contains(lich.getTrangThai())) throw new IllegalArgumentException("Trạng thái không cho phép đổi giờ");
        if (gio == null || !gio.matches("\\d{2}:\\d{2} - \\d{2}:\\d{2}")) throw new IllegalArgumentException("Giờ phải có dạng 08:00 - 08:30");
        if (lichHenRepo.existsByNgayKhamAndKhungGioKhamAndTrangThaiNotAndIdNot(lich.getNgayKham(), gio, "DA_HUY", id)) throw new IllegalArgumentException("Khung giờ đã được đặt");
        lich.setKhungGioKham(gio);
        lichHenRepo.save(lich);
    }

    // -- Lấy lịch sử từ hồ sơ đã hoàn thành, không nhân đôi dữ liệu --
    public List<HoSoBenhAn> lichSuKham(Long lichId) {
        LichHen lich = chiTiet(lichId);
        return hoSoRepo.lichSuKham(lich.getBenhNhan().getId(), lichId);
    }

    public Optional<SinhHieu> sinhHieu(Long lichId) {
        return sinhHieuRepo.findByLichHenId(lichId);
    }

    @Transactional
    public SinhHieu luuSinhHieu(Long lichId, Integer tamThu, Integer tamTruong,
                                 Integer nhipTim, Integer spo2,
                                 java.math.BigDecimal canNang,
                                 java.math.BigDecimal chieuCao) {
        LichHen lich = chiTiet(lichId);
        if (!"DANG_KHAM".equals(lich.getTrangThai())) {
            throw new IllegalArgumentException("Chỉ ca đang khám mới được cập nhật sinh hiệu");
        }
        if (tamThu != null && tamThu <= 0) {
            throw new IllegalArgumentException("Huyết áp tâm thu không hợp lệ");
        }
        if (tamTruong != null && tamTruong <= 0) {
            throw new IllegalArgumentException("Huyết áp tâm trương không hợp lệ");
        }
        if (nhipTim != null && nhipTim <= 0) {
            throw new IllegalArgumentException("Nhịp tim không hợp lệ");
        }
        if (spo2 != null && (spo2 < 0 || spo2 > 100)) {
            throw new IllegalArgumentException("SpO2 phải từ 0 đến 100");
        }
        if (canNang != null && canNang.signum() <= 0) {
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0");
        }
        if (chieuCao != null && chieuCao.signum() <= 0) {
            throw new IllegalArgumentException("Chiều cao phải lớn hơn 0");
        }

        SinhHieu sinhHieu = sinhHieuRepo.findByLichHenId(lichId)
                .orElseGet(SinhHieu::new);
        sinhHieu.setLichHen(lich);
        sinhHieu.setHuyetApTamThu(tamThu);
        sinhHieu.setHuyetApTamTruong(tamTruong);
        sinhHieu.setNhipTim(nhipTim);
        sinhHieu.setSpo2(spo2);
        sinhHieu.setCanNang(canNang);
        sinhHieu.setChieuCao(chieuCao);
        sinhHieu.setThoiGianDo(LocalDateTime.now());
        return sinhHieuRepo.save(sinhHieu);
    }



    public Optional<HoSoBenhAn> hoSo(Long lichId) {
        return hoSoRepo.findByLichHenId(lichId);
    }

    public List<DonThuoc> donThuoc(Long hoSoId) {
        return donThuocRepo.findByHoSoBenhAnId(hoSoId);
    }

    @Transactional
    public void luuKhamDayDu(Long id, String chanDoan, String loiDan, String[] tenThuoc, String[] lieuLuong, Integer[] soLuong, String[] huongDan, boolean hoanThanh, String timMach, String hoHap, String tieuHoa, String chanDoanPhu, String canLamSang, String huongDieuTri, String tienSu, String giaDinh, String thoiQuen) {
        if (!"DANG_KHAM".equals(chiTiet(id).getTrangThai())) throw new IllegalArgumentException("Hồ sơ đã hoàn tất hoặc chưa bắt đầu, không thể chỉnh sửa");
        luuKham(id, chanDoan, loiDan, tenThuoc, lieuLuong, soLuong, huongDan, false);
        HoSoBenhAn hoSo = hoSoRepo.findByLichHenId(id).orElseThrow();
        hoSo.setKhamTimMach(timMach);
        hoSo.setKhamHoHap(hoHap);
        hoSo.setKhamTieuHoa(tieuHoa);
        hoSo.setChanDoanPhu(chanDoanPhu);
        hoSo.setCanLamSang(canLamSang);
        hoSo.setHuongDieuTri(huongDieuTri);
        hoSoRepo.save(hoSo);
        BenhNhan bn = chiTiet(id).getBenhNhan();
        if (tienSu != null) bn.setTienSuBenh(tienSu);
        if (giaDinh != null) bn.setTienSuGiaDinh(giaDinh);
        if (thoiQuen != null) bn.setThoiQuenSinhHoat(thoiQuen);
        benhNhanRepo.save(bn);
        if (hoanThanh) {
            LichHen lich = chiTiet(id);
            lich.setTrangThai("DA_KHAM");
            lichHenRepo.save(lich);
        }
    }

    @Transactional
    public void luuKham(Long lichId, String chanDoan, String loiDan, String[] tenThuoc, String[] lieuLuong, Integer[] soLuong, String[] huongDan, boolean hoanThanh) {
        LichHen lich = chiTiet(lichId);
        if (!"DANG_KHAM".equals(lich.getTrangThai())) throw new IllegalArgumentException("Chỉ lịch đang khám mới được chỉnh sửa hồ sơ");
        if (chanDoan == null || chanDoan.isBlank()) throw new IllegalArgumentException("Chẩn đoán không được để trống");
        HoSoBenhAn hoSo = hoSoRepo.findByLichHenId(lichId).orElseGet(HoSoBenhAn::new);
        hoSo.setLichHen(lich);
        hoSo.setChanDoan(chanDoan.trim());
        hoSo.setLoiDanBacSi(loiDan);
        if (hoSo.getNgayTao() == null) hoSo.setNgayTao(LocalDateTime.now());
        hoSo = hoSoRepo.save(hoSo);
        donThuocRepo.deleteByHoSoBenhAnId(hoSo.getId());
        donThuocRepo.flush();
        if (tenThuoc != null) for (int i = 0; i < tenThuoc.length; i++) {
            if (tenThuoc[i] == null || tenThuoc[i].isBlank()) continue;
            if (lieuLuong == null || i >= lieuLuong.length || lieuLuong[i].isBlank() || soLuong == null || i >= soLuong.length || soLuong[i] == null || soLuong[i] <= 0) throw new IllegalArgumentException("Đơn thuốc phải có liều lượng và số lượng hợp lệ");
            DonThuoc don = new DonThuoc();
            don.setHoSoBenhAn(hoSo);
            don.setTenThuoc(tenThuoc[i].trim());
            don.setLieuLuong(lieuLuong[i]);
            don.setSoLuong(soLuong[i]);
            don.setHuongDanSuDung(huongDan != null && i < huongDan.length ? huongDan[i] : "");
            donThuocRepo.save(don);
        }
        if (hoanThanh) {
            lich.setTrangThai("DA_KHAM");
            lichHenRepo.save(lich);
        }
    }
}
