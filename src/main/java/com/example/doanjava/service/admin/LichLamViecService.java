package com.example.doanjava.service.admin;

import com.example.doanjava.dto.admin.TaoCaDTO;
import com.example.doanjava.entity.BacSi;
import com.example.doanjava.entity.LichLamViec;
import com.example.doanjava.entity.PhongKham;
import com.example.doanjava.repository.BacSiRepository;
import com.example.doanjava.repository.LichHenRepository;
import com.example.doanjava.repository.LichLamViecRepository;
import com.example.doanjava.repository.PhongKhamRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LichLamViecService {

    // Giờ của từng ca. Muốn đổi giờ ca thì sửa tại đây.
    public static final LocalTime SANG_BAT_DAU = LocalTime.of(7, 30);
    public static final LocalTime SANG_KET_THUC = LocalTime.of(11, 30);
    public static final LocalTime CHIEU_BAT_DAU = LocalTime.of(13, 30);
    public static final LocalTime CHIEU_KET_THUC = LocalTime.of(17, 0);

    private static final int TOI_DA_SO_NGAY = 84;   // 12 tuần mỗi lần tạo
    private static final DateTimeFormatter NGAY_THANG = DateTimeFormatter.ofPattern("dd/MM");

    private final LichLamViecRepository caRepo;
    private final BacSiRepository bacSiRepo;
    private final PhongKhamRepository phongKhamRepo;
    private final LichHenRepository lichHenRepo;

    public LichLamViecService(LichLamViecRepository caRepo,
                              BacSiRepository bacSiRepo,
                              PhongKhamRepository phongKhamRepo,
                              LichHenRepository lichHenRepo) {
        this.caRepo = caRepo;
        this.bacSiRepo = bacSiRepo;
        this.phongKhamRepo = phongKhamRepo;
        this.lichHenRepo = lichHenRepo;
    }

    // ---------- Dữ liệu cho trang ----------

    public List<LichLamViec> layTheoTuan(LocalDate tu, LocalDate den, Long bacSiId) {
        return caRepo.locTheoKhoangNgay(tu, den, bacSiId);
    }

    /** Số lịch hẹn đang giữ chỗ (không tính đã hủy) của từng ca. */
    public Map<Long, Long> demDaDat(List<LichLamViec> dsCa) {
        Map<Long, Long> map = new HashMap<>();
        for (LichLamViec ca : dsCa) {
            map.put(ca.getId(), lichHenRepo.countByLichLamViecIdAndTrangThaiNot(ca.getId(), "DA_HUY"));
        }
        return map;
    }

    public List<BacSi> dsBacSi() {
        return bacSiRepo.findAll(Sort.by("hoTen"));
    }

    public List<PhongKham> dsPhong() {
        return phongKhamRepo.findAll(Sort.by("tenPhong"));
    }

    // ---------- Tạo ca hàng loạt ----------

    @Transactional
    public int taoHangLoat(TaoCaDTO dto) {
        if (dto.getBacSiId() == null) throw new IllegalArgumentException("Vui lòng chọn bác sĩ!");
        if (dto.getPhongKhamId() == null) throw new IllegalArgumentException("Vui lòng chọn phòng khám!");
        if (dto.getTuNgay() == null || dto.getDenNgay() == null)
            throw new IllegalArgumentException("Vui lòng chọn khoảng ngày!");
        if (dto.getTuNgay().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Không thể tạo ca trong quá khứ!");
        if (dto.getDenNgay().isBefore(dto.getTuNgay()))
            throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!");
        if (ChronoUnit.DAYS.between(dto.getTuNgay(), dto.getDenNgay()) > TOI_DA_SO_NGAY)
            throw new IllegalArgumentException("Mỗi lần chỉ được tạo tối đa 12 tuần!");
        if (dto.getThu() == null || dto.getThu().isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một ngày trong tuần!");
        if (dto.getCaLam() == null || dto.getCaLam().isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một ca (sáng hoặc chiều)!");

        int toiDa = dto.getSoLuongToiDa() == null ? 10 : dto.getSoLuongToiDa();
        if (toiDa < 1 || toiDa > 100)
            throw new IllegalArgumentException("Số lượng bệnh nhân tối đa phải từ 1 đến 100!");

        BacSi bacSi = bacSiRepo.findById(dto.getBacSiId())
                .orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại!"));
        PhongKham phong = phongKhamRepo.findById(dto.getPhongKhamId())
                .orElseThrow(() -> new IllegalArgumentException("Phòng khám không tồn tại!"));

        if (Boolean.FALSE.equals(bacSi.getTrangThai()))
            throw new IllegalArgumentException("Bác sĩ đang tạm nghỉ, không thể xếp ca!");
        if (Boolean.FALSE.equals(phong.getTrangThai()))
            throw new IllegalArgumentException("Phòng khám đang bảo trì, không thể xếp ca!");
        if (bacSi.getChuyenKhoa() == null || phong.getChuyenKhoa() == null
                || !Objects.equals(bacSi.getChuyenKhoa().getId(), phong.getChuyenKhoa().getId()))
            throw new IllegalArgumentException("Phòng khám không thuộc chuyên khoa của bác sĩ này!");

        // Duyệt từng ngày, từng ca: gom ca hợp lệ và ca bị trùng
        List<LichLamViec> canTao = new ArrayList<>();
        List<String> xungDot = new ArrayList<>();

        for (LocalDate ngay = dto.getTuNgay(); !ngay.isAfter(dto.getDenNgay()); ngay = ngay.plusDays(1)) {
            if (!dto.getThu().contains(ngay.getDayOfWeek().getValue())) continue;

            for (String ma : dto.getCaLam()) {
                String tenCa;
                LocalTime batDau;
                LocalTime ketThuc;
                if ("SANG".equals(ma)) {
                    tenCa = "Ca sáng";
                    batDau = SANG_BAT_DAU;
                    ketThuc = SANG_KET_THUC;
                } else if ("CHIEU".equals(ma)) {
                    tenCa = "Ca chiều";
                    batDau = CHIEU_BAT_DAU;
                    ketThuc = CHIEU_KET_THUC;
                } else {
                    continue;
                }

                String nhan = tenCa + " " + ngay.format(NGAY_THANG);
                if (caRepo.demTrungBacSi(bacSi.getId(), ngay, batDau, ketThuc) > 0) {
                    xungDot.add(nhan + " (bác sĩ đã có ca khác)");
                    continue;
                }
                if (caRepo.demTrungPhong(phong.getId(), ngay, batDau, ketThuc) > 0) {
                    xungDot.add(nhan + " (phòng đã có bác sĩ khác)");
                    continue;
                }

                LichLamViec ca = new LichLamViec();
                ca.setBacSi(bacSi);
                ca.setPhongKham(phong);
                ca.setNgayLamViec(ngay);
                ca.setTenCa(tenCa);
                ca.setGioBatDau(batDau);
                ca.setGioKetThuc(ketThuc);
                ca.setSoLuongToida(toiDa);
                canTao.add(ca);
            }
        }

        // Tất cả hoặc không gì cả: có ca trùng thì không tạo ca nào
        if (!xungDot.isEmpty()) {
            String vd = xungDot.stream().limit(3).collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Có " + xungDot.size() + " ca bị trùng lịch: " + vd
                    + (xungDot.size() > 3 ? "..." : "") + ". Chưa có ca nào được tạo.");
        }
        if (canTao.isEmpty())
            throw new IllegalArgumentException("Không có ngày nào phù hợp trong khoảng đã chọn!");

        caRepo.saveAll(canTao);
        return canTao.size();
    }

    // ---------- Xóa ca ----------

    @Transactional
    public void xoa(Long id) {
        LichLamViec ca = caRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca làm việc!"));
        // Tính cả lịch đã hủy: bảng lich_hen tham chiếu ca này (ON DELETE NO ACTION)
        if (lichHenRepo.countByLichLamViecId(id) > 0)
            throw new IllegalArgumentException("Ca này đã có lịch hẹn của bệnh nhân nên không thể xóa!");
        caRepo.delete(ca);
    }
}