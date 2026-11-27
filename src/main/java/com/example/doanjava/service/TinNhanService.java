package com.example.doanjava.service;

import com.example.doanjava.dto.CuocTroChuyenDTO;
import com.example.doanjava.entity.CuocTroChuyen;
import com.example.doanjava.entity.TaiKhoan;
import com.example.doanjava.entity.TinNhan;
import com.example.doanjava.repository.CuocTroChuyenRepository;
import com.example.doanjava.repository.LichHenRepository;
import com.example.doanjava.repository.TaiKhoanRepository;
import com.example.doanjava.repository.TinNhanRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TinNhanService {
    private static final int KI_TU_TOI_DA = 1000;

    private static final String VAI_TRO_ADMIN ="ADMIN";
    private static final String VAI_TRO_BAC_SI = "BAC_SI";
    private static final String VAI_TRO_BENH_NHAN = "BENH_NHAN";

    private final CuocTroChuyenRepository cuocTroChuyenRepository;
    private final TinNhanRepository tinNhanRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final LichHenRepository lichHenRepository;

    public TinNhanService(CuocTroChuyenRepository ctt, TinNhanRepository tn, TaiKhoanRepository tk, LichHenRepository lh)
    {
        this.cuocTroChuyenRepository = ctt;
        this.tinNhanRepository = tn;
        this.taiKhoanRepository = tk;
        this.lichHenRepository = lh;
    }

    public boolean duocNhanTin(TaiKhoan taiKhoan1, TaiKhoan taiKhoan2)
    {
        String vaiTro1 = taiKhoan1.getVaiTro();
        String vaiTro2 = taiKhoan2.getVaiTro();

        boolean isAdmin = vaiTro1.equals(VAI_TRO_ADMIN) || vaiTro2.equals(VAI_TRO_ADMIN);
        if(isAdmin)
        {
            return  true;
        }

        boolean BenhNhan_BacSi = vaiTro1.equals(VAI_TRO_BENH_NHAN) && vaiTro2.equals(VAI_TRO_BAC_SI);
        boolean BacSi_BenhNhan = vaiTro1.equalsIgnoreCase(VAI_TRO_BAC_SI) && vaiTro2.equals(VAI_TRO_BENH_NHAN);

        if(!BacSi_BenhNhan && !BenhNhan_BacSi)
        {
            return  false;
        }

        TaiKhoan taiKhoanBenhNhan;
        TaiKhoan taiKhoanBacSi;

        if(BenhNhan_BacSi)
        {
            taiKhoanBenhNhan = taiKhoan1;
            taiKhoanBacSi = taiKhoan2;
        }
        else {
            taiKhoanBenhNhan = taiKhoan2;
            taiKhoanBacSi = taiKhoan1;
        }

        if(taiKhoanBenhNhan.getBenhNhan() == null || taiKhoanBacSi.getBacSi() == null)
        {
            return false;
        }

        Long idBenhNhan = taiKhoanBenhNhan.getBenhNhan().getId();
        Long idBacSi = taiKhoanBacSi.getBacSi().getId();

        return lichHenRepository.existsByBenhNhanIdAndLichLamViec_BacSiIdAndTrangThaiNot(idBenhNhan, idBacSi, "DA_HUY");
    }

    // Lấy cuộc trò chuyện, nếu chưa có thì tạo mới
    @Transactional
    public CuocTroChuyen layHoacTao(Long idNguoiDung, Long idDoiPhuong)
    {
        // B1: Kiểm tra dữ liệu
        if(idDoiPhuong == null || idNguoiDung == null)
        {
            throw new IllegalArgumentException("Thiếu thông tin người trò chuyện");
        }

        if(idNguoiDung.equals(idDoiPhuong))
        {
            throw  new IllegalArgumentException("Không thể tự nhắn cho chính mình");
        }

        //B2: Lấy tài khoản
        TaiKhoan nguoiDung = taiKhoanRepository.findById(idNguoiDung)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản của bạn!"));
        TaiKhoan doiPhuong = taiKhoanRepository.findById(idDoiPhuong)
                .orElseThrow(() -> new IllegalArgumentException("Người nhận không tồn tại!"));

        //B3: Kiểm tra quyền nhắn tin
        if(!Boolean.TRUE.equals(doiPhuong.getTrangThai()))
        {
            throw new IllegalArgumentException("Tài khoản người nhận bị khóa");
        }
        if(!duocNhanTin(nguoiDung, doiPhuong))
        {
            throw new IllegalArgumentException("Bạn chỉ có thể nhắn tin với người đã có lịch hẹn với bạn!");
        }

        //B4: Sắp xếp id
        Long idNho = Math.min(nguoiDung.getId(), doiPhuong.getId());
        long idLon = Math.max(nguoiDung.getId(), doiPhuong.getId());

        //B5: [TH1] Đã có trò chuyện
        Optional<CuocTroChuyen> troChuyenDaTonTai = cuocTroChuyenRepository.timTheoHaiTaiKhoan(idNho, idLon);
        if(troChuyenDaTonTai.isPresent())
        {
            return  troChuyenDaTonTai.get();
        }

        //B6: [TH2] chưa có trò chuyện
        CuocTroChuyen troChuyen = new CuocTroChuyen();
        boolean nguoiDungMin = idNguoiDung.equals(idNho);
        if(nguoiDungMin)
        {
            troChuyen.setTaiKhoanNhoHon(nguoiDung);
            troChuyen.setTaiKhoanLonHon(doiPhuong);
        }
        else
        {
            troChuyen.setTaiKhoanLonHon(nguoiDung);
            troChuyen.setTaiKhoanNhoHon(doiPhuong);
        }

        troChuyen.setNgayTao(LocalDateTime.now());
        return cuocTroChuyenRepository.save(troChuyen);
    }

    @Transactional(readOnly = true)
    public List<CuocTroChuyenDTO> layDanhSach(Long idNguoiDung)
    {
        List<CuocTroChuyenDTO> danhSach = new ArrayList<>();

        for(CuocTroChuyen troChuyen : cuocTroChuyenRepository.timTheoNguoiDung(idNguoiDung))
        {
            TaiKhoan doiPhuong = troChuyen.layDoiPhuong(idNguoiDung);
            TinNhan tinCuoi = tinNhanRepository.findFirstByCuocTroChuyenIdOrderByThoiGianGuiDescIdDesc(troChuyen.getId()).orElse(null);

            LocalDateTime thoiGianCuoi = LocalDateTime.MIN;
            if(tinCuoi != null && tinCuoi.getThoiGianGui() != null)
            {
                thoiGianCuoi = tinCuoi.getThoiGianGui();
            }
            else if(troChuyen.getNgayTao() != null)
            {
                thoiGianCuoi = troChuyen.getNgayTao();
            }

            CuocTroChuyenDTO dong = new CuocTroChuyenDTO();
            dong.setCuocId(troChuyen.getId());
            dong.setDoiPhuongId(doiPhuong.getId());
            dong.setTenDoiPhuong(layTen(doiPhuong));
            dong.setVaiTroDoiPhuong(layNhanVaiTro(doiPhuong));
            dong.setTinCuoi(tinCuoi != null ? tinCuoi.getNoiDung() : null);
            dong.setThoiGianCuoi(thoiGianCuoi);
            dong.setSoChuaDoc(tinNhanRepository.demChuaDoc(troChuyen.getId(), idNguoiDung));
            danhSach.add(dong);
        }
        danhSach.sort((truoc, sau) -> sau.getThoiGianCuoi().compareTo(truoc.getThoiGianCuoi()));

        return danhSach;
    }

    @Transactional
    public TinNhan guiTinNhan(Long idNguoiDung, Long idTroChuyen, String noiDungNhap)
    {
        String noiDung = (noiDungNhap == null) ? "" : noiDungNhap.trim();

        if(noiDung.isEmpty())
        {
            throw new IllegalArgumentException("Hãy nhập tin nhắn");
        }

        if(noiDung.length() > KI_TU_TOI_DA)
        {
            throw  new IllegalArgumentException("Tin nhắn tối đa " +KI_TU_TOI_DA + " kí tự");
        }

        CuocTroChuyen troChuyen = layTroChuyenCuaThanhVien(idNguoiDung, idTroChuyen);

        TaiKhoan doiPhuong = troChuyen.layDoiPhuong(idNguoiDung);
        if(!Boolean.TRUE.equals(doiPhuong.getTrangThai()))
        {
            throw new IllegalArgumentException("Tài khoản của người nhận hiện đang bị khóa");
        }

        TinNhan tinNhan = new TinNhan();
        tinNhan.setCuocTroChuyen(troChuyen);
        tinNhan.setNguoiGui(troChuyen.layNguoiDung(idNguoiDung));
        tinNhan.setNoiDung(noiDung);
        tinNhan.setThoiGianGui(LocalDateTime.now());
        tinNhan.setDaDoc(false);
        return tinNhanRepository.save(tinNhan);
    }

    @Transactional(readOnly = true)
    public List<TinNhan> layLichSu(Long idNguoiDung, Long idTroChuyen, int page, int size)
    {
        layTroChuyenCuaThanhVien(idNguoiDung, idTroChuyen);

        PageRequest trangYeuCau = PageRequest.of(Math.max(0, page), size);

        List<TinNhan> danhSachTinNhan = new ArrayList<>(
                tinNhanRepository.findByCuocTroChuyenIdOrderByThoiGianGuiDescIdDesc(idTroChuyen, trangYeuCau)
                        .getContent());

        Collections.reverse(danhSachTinNhan);
        return danhSachTinNhan;
    }

    @Transactional
    public int danhDauDaDoc(Long idNguoiDung, Long idTroChuyen)
    {
        layTroChuyenCuaThanhVien(idNguoiDung, idTroChuyen);
        return tinNhanRepository.danhDauDaDoc(idTroChuyen, idNguoiDung);

    }

    public CuocTroChuyen layTroChuyenCuaThanhVien(Long idNguoiDung, Long idCuoc) {
        CuocTroChuyen cuoc = cuocTroChuyenRepository.findById(idCuoc)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc trò chuyện!"));
        if (!cuoc.laThanhVien(idNguoiDung)) {
            throw new IllegalArgumentException("Bạn không thuộc cuộc trò chuyện này!");
        }
        return cuoc;
    }

    private String layNhanVaiTro(TaiKhoan taiKhoan)
    {
        String vaiTro = taiKhoan.getVaiTro();
        if(vaiTro.equals(VAI_TRO_BENH_NHAN))
        {
            return "Bệnh nhân";
        }
        if(vaiTro.equals(VAI_TRO_BAC_SI))
        {
            return "Bác sĩ";
        }
        return "Admin";
    }

    private String layTen(TaiKhoan taiKhoan)
    {
        String vaiTro = taiKhoan.getVaiTro();
        if(vaiTro.equals(VAI_TRO_BAC_SI) && taiKhoan.getBacSi() != null)
        {
            return taiKhoan.getBacSi().getHoTen();
        }
        if(vaiTro.equals(VAI_TRO_BENH_NHAN) && taiKhoan.getBenhNhan() != null)
        {
            return  taiKhoan.getBenhNhan().getHoTen();
        }

        return "Quản trị viên (" + taiKhoan.getTenDangNhap() + ")";
    }

    public long demTinChuaDoc(Long idNguoiDung)
    {
        return tinNhanRepository.demChuaDocCuaNguoiDung(idNguoiDung);
    }

    @Transactional(readOnly = true)
    public String layTenHienThi(Long idTaiKhoan) {
        if (idTaiKhoan == null) {
            return "Người dùng";
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findById(idTaiKhoan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản!"));
        return layTen(taiKhoan);
    }
}
