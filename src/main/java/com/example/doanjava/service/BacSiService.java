package com.example.doanjava.service;

import com.example.doanjava.dto.admin.BacSiDTO;
import com.example.doanjava.dto.admin.SuaBacSiDTO;
import com.example.doanjava.entity.*;
import com.example.doanjava.repository.*;
import com.example.doanjava.service.admin.TaiKhoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BacSiService {
    private final BacSiRepository bacSiRepository;
    private final ChuyenKhoaRepository chuyenKhoaRepository;
    private final LichLamViecRepository lichLamViecRepository;
    private final HomeService homeService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PhongKhamRepository phongKhamRepository;

    public BacSiService(BacSiRepository bacSiRepository,
                        ChuyenKhoaRepository chuyenKhoaRepository,
                        LichLamViecRepository lichLamViecRepository,
                        HomeService homeService,
                        TaiKhoanRepository taiKhoanRepository,
                        PhongKhamRepository phongKhamRepository) {
        this.bacSiRepository = bacSiRepository;
        this.chuyenKhoaRepository = chuyenKhoaRepository;
        this.lichLamViecRepository = lichLamViecRepository;
        this.homeService = homeService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.phongKhamRepository= phongKhamRepository;
    }

    public List<Map<String, Object>> layDanhSachBacSi() {
        return bacSiRepository.findAll(Sort.by(Sort.Direction.ASC, "hoTen"))
                .stream().map(homeService::bacSiThanhMap).toList();
    }

    public List<Map<String, Object>> layDanhSachChuyenKhoa() {
        return chuyenKhoaRepository.findAll(Sort.by(Sort.Direction.ASC, "tenChuyenKhoa"))
                .stream().map(homeService::chuyenKhoaThanhMap).toList();
    }

    public List<String> layDanhSachHocVi()
    {
        return bacSiRepository.findAll().stream()
                .map(BacSi::getHocHamHocVi)
                .filter(hv -> hv!= null && !hv.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .sorted()
                .toList();
    }

    public Map<Long, List<Map<String, Object>>> layLichSapToi() {
        LocalDate homNay = LocalDate.now();
        List<LichLamViec> lichLamViecs = lichLamViecRepository
                .findByNgayLamViecBetween(homNay, homNay.plusMonths(6));

        Map<Long, List<Map<String, Object>>> ketQua = new LinkedHashMap<>();
        for (LichLamViec lich : lichLamViecs) {
            if (lich.getBacSi() == null || lich.getBacSi().getId() == null) continue;
            Map<String, Object> dongLich = new LinkedHashMap<>();
            dongLich.put("ngayLamViec", lich.getNgayLamViec());
            dongLich.put("tenCa", lich.getTenCa());
            dongLich.put("gioBatDau", lich.getGioBatDau());
            dongLich.put("gioKetThuc", lich.getGioKetThuc());
            ketQua.computeIfAbsent(lich.getBacSi().getId(), id -> new ArrayList<>()).add(dongLich);
        }
        return ketQua;
    }

    public Map<Long, Map<String, Object>> layThongKeChuyenKhoa() {
        return homeService.layThongKeChuyenKhoa();
    }

    public Page<BacSi> LayDSHoacTimKiem(
            String search,
            String chuyenKhoaId,
            String hocVi,
            Boolean trangThai,
            int page,
            int size
    )
    {
        Specification<BacSi> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm kiếm theo tên bác sĩ
            if(search != null && !search.trim().isEmpty())
            {
                String keyword = "%" + search.trim().toLowerCase() + "%";
                Predicate matchTen = criteriaBuilder.like(criteriaBuilder.lower(root.get("hoTen")), keyword);
                predicates.add(criteriaBuilder.or(matchTen));
            }

            // 2. Lọc theo Chuyên khoa (Dùng biến id kiểu Long đã parse, tuyệt đối không dùng chuyenKhoaId kiểu String)
            if(chuyenKhoaId != null && !chuyenKhoaId.trim().isEmpty())
            {
                try {
                    Long id = Long.valueOf(chuyenKhoaId);
                    predicates.add(criteriaBuilder.equal(root.get("chuyenKhoa").get("id"), id));
                }
                catch (NumberFormatException ignored){}
            }

            // 3. Lọc theo học vị / học hàm
            if (hocVi != null && !hocVi.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("hocHamHocVi"), hocVi));
            }

            // 4. Lọc theo trạng thái bác sĩ (Đang nhận lịch / Tạm nghỉ)
            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            // 5. Chỉ lấy các bác sĩ có tài khoản đang hoạt động (không bị tạm khóa)
            predicates.add(criteriaBuilder.isNotNull(root.get("taiKhoan")));
            predicates.add(criteriaBuilder.isTrue(root.get("taiKhoan").get("trangThai")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "id"));
        return bacSiRepository.findAll(spec, pageable);
    }

    public BacSi LayTheoId(Long id) {return bacSiRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Không tìm thấy bác sĩ"));}

    public Map<Long, List<PhongKham>> layPhongKhamTheoChuyenKhoa(List<BacSi> dsBacSi) {
        Map<Long, List<PhongKham>> mapPhong = new HashMap<>();
        if (dsBacSi == null || dsBacSi.isEmpty()) {
            return mapPhong;
        }

        List<Long> chuyenKhoaIds = dsBacSi.stream()
                .map(bs -> bs.getChuyenKhoa() != null ? bs.getChuyenKhoa().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (chuyenKhoaIds.isEmpty()) {
            return mapPhong;
        }

        for (PhongKham pk : phongKhamRepository.findByChuyenKhoa_IdIn(chuyenKhoaIds)) {
            if (pk.getChuyenKhoa() != null && pk.getChuyenKhoa().getId() != null) {
                mapPhong.computeIfAbsent(pk.getChuyenKhoa().getId(), k -> new ArrayList<>()).add(pk);
            }
        }
        return mapPhong;
    }

    @Transactional
    public void Them(BacSiDTO bacSiDTO)
    {
        String tenDangNhap = bacSiDTO.getTenDangNhap() != null ? bacSiDTO.getTenDangNhap().trim() : "";
        String email = bacSiDTO.getEmail() != null ? bacSiDTO.getEmail().trim().toLowerCase() : "";

        if(tenDangNhap.isEmpty())
        {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (taiKhoanRepository.existsByTenDangNhap(tenDangNhap)) {
            throw new IllegalArgumentException("Tên đăng nhập '" + tenDangNhap + "' đã tồn tại");
        }
        if (taiKhoanRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email '" + email + "' đã được sử dụng");
        }

        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(bacSiDTO.getTenDangNhap().trim());
        tk.setMatKhau(bacSiDTO.getMatKhau());
        tk.setEmail(bacSiDTO.getEmail());
        tk.setSoDienThoai(bacSiDTO.getSoDienThoai().trim());
        tk.setVaiTro("BAC_SI");
        tk.setTrangThai(true);
        tk.setNgayTao(LocalDateTime.now());
        TaiKhoan luu = taiKhoanRepository.save(tk);

        ChuyenKhoa ck = chuyenKhoaRepository.findById(bacSiDTO.getChuyenKhoaId()).orElseThrow(()->new IllegalArgumentException("Chuyên khoa không tồn tại"));

        BacSi bacSi = new BacSi();
        bacSi.setTaiKhoan(luu);
        bacSi.setChuyenKhoa(ck);
        bacSi.setHoTen(bacSiDTO.getHoTen().trim());
        bacSi.setHocHamHocVi(bacSiDTO.getHocHamHocVi());
        bacSi.setSoNamKinhNghiem(bacSiDTO.getSoNamKinhNghiem());
        bacSi.setGioiThieu(bacSiDTO.getGioiThieu());
        bacSi.setTrangThai(bacSiDTO.getTrangThai() == null || bacSiDTO.getTrangThai());

        bacSiRepository.save(bacSi);
    }

    public void Xoa(Long id)
    {
        bacSiRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public SuaBacSiDTO layThongTinSua(Long id) {
        BacSi bs = bacSiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ có ID: " + id));

        SuaBacSiDTO dto = new SuaBacSiDTO();
        dto.setId(bs.getId());
        dto.setHoTen(bs.getHoTen());
        dto.setChuyenKhoaId(bs.getChuyenKhoa() != null ? bs.getChuyenKhoa().getId() : null);
        dto.setHocHamHocVi(bs.getHocHamHocVi());
        dto.setSoNamKinhNghiem(bs.getSoNamKinhNghiem());
        dto.setGioiThieu(bs.getGioiThieu());
        dto.setTrangThai(bs.getTrangThai());

        if (bs.getTaiKhoan() != null) {
            dto.setEmailMoi(bs.getTaiKhoan().getEmail());
            dto.setSoDienThoaiMoi(bs.getTaiKhoan().getSoDienThoai());
            dto.setTrangThai(bs.getTrangThai());
        }
        return dto;
    }

    @Transactional
    public void capNhatBacSi(SuaBacSiDTO dto) {
        BacSi bs = bacSiRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ có ID: " + dto.getId()));

        ChuyenKhoa ck = chuyenKhoaRepository.findById(dto.getChuyenKhoaId())
                .orElseThrow(() -> new IllegalArgumentException("Chuyên khoa không tồn tại"));

        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ và tên bác sĩ không được để trống");
        }

        // Cập nhật thông tin bảng bac_si
        bs.setHoTen(dto.getHoTen().trim());
        bs.setChuyenKhoa(ck);
        bs.setHocHamHocVi(dto.getHocHamHocVi());
        bs.setSoNamKinhNghiem(dto.getSoNamKinhNghiem());
        bs.setGioiThieu(dto.getGioiThieu());
        if (dto.getTrangThai() != null) {
            bs.setTrangThai(dto.getTrangThai());
        }

        // Cập nhật thông tin bảng tai_khoan nếu có liên kết
        TaiKhoan tk = bs.getTaiKhoan();
        if (tk != null) {
            if (dto.getEmailMoi() != null && !dto.getEmailMoi().trim().isEmpty()) {
                String emailMoi = dto.getEmailMoi().trim().toLowerCase();
                if (!emailMoi.equalsIgnoreCase(tk.getEmail()) && taiKhoanRepository.existsByEmailIgnoreCase(emailMoi)) {
                    throw new IllegalArgumentException("Email '" + emailMoi + "' đã được sử dụng bởi tài khoản khác");
                }
                tk.setEmail(emailMoi);
            }
            if (dto.getSoDienThoaiMoi() != null) {
                tk.setSoDienThoai(dto.getSoDienThoaiMoi().trim());
            }

            if (dto.getMatKhauMoi() != null && !dto.getMatKhauMoi().trim().isEmpty()) {
                tk.setMatKhau(dto.getMatKhauMoi().trim());
            }
            taiKhoanRepository.save(tk);
        }

        bacSiRepository.save(bs);
    }

    public void Luu(BacSi bs)
    {
        bacSiRepository.save(bs);
    }
}
