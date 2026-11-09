
package com.example.doanjava.service;
import com.example.doanjava.entity.*;
import com.example.doanjava.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
@Service
public class LichHenService {
    private final LichHenRepository lichHenRepo;
    @org.springframework.beans.factory.annotation.Autowired private PhongKhamRepository phongKhamRepo;
    @org.springframework.beans.factory.annotation.Autowired private DichVuKhamRepository dichVuKhamRepo;
    @org.springframework.beans.factory.annotation.Autowired private ThuocRepository thuocRepo;
    @org.springframework.beans.factory.annotation.Autowired
    private SinhHieuRepository sinhHieuRepo;
    @org.springframework.beans.factory.annotation.Autowired
    private ThanhToanRepository thanhToanRepo;
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

    public Page<LichHen> danhSach(String search, String status, LocalDate from, LocalDate to, int page) {
        return lichHenRepo.timKiem(search == null ? "" : search.trim(), status == null || status.isBlank() ? "ALL" : status, from, to, PageRequest.of(Math.max(0, page - 1), 8, Sort.by("ngayKham").descending().and(Sort.by("id").descending())));
    }

    public LichHen chiTiet(Long id) {
        return lichHenRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn " + id));
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
        return lichHenRepo.countByTrangThai(status);
    }

    // -- Thống kê riêng số lịch có ngày khám là hôm nay --
    public long tongHomNay() {
        return lichHenRepo.countByNgayKham(LocalDate.now());
    }

    public List<LichHen> thongBao() {
        return lichHenRepo.findTop10ByOrderByNgayTaoDesc();
    }

    public List<PhongKham> phongKhams() { return phongKhamRepo.findAll(Sort.by("tenPhong")); }
    public List<DichVuKham> dichVuKhams() { return dichVuKhamRepo.findAll(Sort.by("tenDichVu")); }
    public List<Thuoc> thuocs() { return thuocRepo.findAll(Sort.by("tenThuoc")); }

    @Transactional
    public LichHen tao(Long benhNhanId, Long bacSiId, Long caId, LocalDate ngay, String gio, String trieuChung) {
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
    public LichHen taoDayDu(Long benhNhanId, Long bacSiId, Long caId, LocalDate ngay, String gio, String trieuChung, Long phongId, Long dichVuId, String ghiChu, Integer tamThu, Integer tamTruong, Integer nhipTim, Integer spo2, java.math.BigDecimal canNang, java.math.BigDecimal chieuCao, String tienSu, String giaDinh, String thoiQuen) {
        if (phongId == null || dichVuId == null) throw new IllegalArgumentException("Phải chọn phòng khám và dịch vụ khám");
        PhongKham phong = phongKhamRepo.findById(phongId).orElseThrow(() -> new IllegalArgumentException("Phòng khám không tồn tại"));
        DichVuKham dichVu = dichVuKhamRepo.findById(dichVuId).orElseThrow(() -> new IllegalArgumentException("Dịch vụ không tồn tại"));
        if (spo2 != null && (spo2 < 0 || spo2 > 100)) throw new IllegalArgumentException("SpO2 phải từ 0 đến 100");
        if (canNang != null && canNang.signum() <= 0 || chieuCao != null && chieuCao.signum() <= 0) throw new IllegalArgumentException("Cân nặng và chiều cao phải lớn hơn 0");
        LichHen lich = tao(benhNhanId, bacSiId, caId, ngay, gio, trieuChung);
        lich.setPhongKham(phong);
        lich.setDichVuKham(dichVu);
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
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setLichHen(lich);
        thanhToan.setSoTien(dichVu.getGiaKham() == null ? java.math.BigDecimal.ZERO : dichVu.getGiaKham());
        thanhToan.setTrangThai("CHUA_THANH_TOAN");
        thanhToanRepo.save(thanhToan);
        return lich;
    }

    @Transactional
    public void doiTrangThai(Long id, String hanhDong) {
        LichHen lich = chiTiet(id);
        String hienTai = lich.getTrangThai();
        String moi = switch (hanhDong) {
            case "duyet" -> "CHO_XAC_NHAN".equals(hienTai) ? "DA_XAC_NHAN" : null;
            case "tuchoi" -> "CHO_XAC_NHAN".equals(hienTai) ? "DA_HUY" : null;
            case "batdaukham" -> "DA_XAC_NHAN".equals(hienTai) ? "DANG_KHAM" : null;
            default -> null;
        };
        if (moi == null) throw new IllegalArgumentException("Không thể thực hiện thao tác với trạng thái hiện tại");
        lich.setTrangThai(moi);
        lichHenRepo.save(lich);
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

    public Optional<ThanhToan> thanhToan(Long lichId) {
        return thanhToanRepo.findByLichHenId(lichId);
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
