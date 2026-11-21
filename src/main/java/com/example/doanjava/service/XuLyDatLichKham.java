
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

        try {
            maLichHen = datLichKhamService.taoLichHen(
                    benhNhanId, thongTinDatLich);

        } catch (RuntimeException ex) {
            loi = ex;
        }
    }

    public Long getMaLichHen() {
        return maLichHen;
    }

    public RuntimeException getLoi() {
        return loi;
    }
}
