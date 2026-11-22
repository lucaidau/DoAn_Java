
package com.example.doanjava.service;

import com.example.doanjava.dto.AppointmentForm;

public class XuLyDatLichKham implements Runnable {

    private final DatLichKhamService datLichKhamService;
    private final Long benhNhanId;
    private final AppointmentForm thongTinDatLich;

    private volatile Long maLichHen;
    private volatile RuntimeException loi;

    public XuLyDatLichKham(
            DatLichKhamService datLichKhamService,
            Long benhNhanId,
            AppointmentForm thongTinDatLich) {

        this.datLichKhamService = datLichKhamService;
        this.benhNhanId = benhNhanId;
        this.thongTinDatLich = thongTinDatLich;
    }


    @Override
    public void run() {

        String tenLuong = Thread.currentThread().getName();

        System.out.println("======================================");
        System.out.println("[BAT DAU] Xu ly dat lich kham");
        System.out.println("[TEN LUONG] " + tenLuong);
        System.out.println("[BENH NHAN ID] " + benhNhanId);

        try {

            maLichHen = datLichKhamService.taoLichHen(
                    benhNhanId, thongTinDatLich);

            System.out.println("[THANH CONG] Dat lich kham");
            System.out.println("[MA LICH HEN] " + maLichHen);
            System.out.println("[TEN LUONG] " + tenLuong);

        } catch (RuntimeException ex) {

            loi = ex;

            System.out.println("[THAT BAI] " + ex.getMessage());
            System.out.println("[TEN LUONG] " + tenLuong);

        } finally {

            System.out.println("[KET THUC] " + tenLuong);
            System.out.println("======================================");
        }
    }


    public Long getMaLichHen() {
        return maLichHen;
    }

    public RuntimeException getLoi() {
        return loi;
    }
}
