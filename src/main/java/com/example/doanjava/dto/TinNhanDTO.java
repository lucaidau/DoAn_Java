package com.example.doanjava.dto;

import com.example.doanjava.entity.TinNhan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TinNhanDTO {
    private Long id;
    private Long cuocTroChuyenId;
    private Long nguoiGuiId;
    private String tenNguoiGui;
    private String noiDung;
    private LocalDateTime thoiGianGui;
    private boolean daDoc;
    private boolean laCuaToi;

    public static TinNhanDTO tuEntity(TinNhan tinNhan, Long idNguoiXem, String tenNguoiGui) {
        Long nguoiGuiId = tinNhan.getNguoiGui() != null ? tinNhan.getNguoiGui().getId() : null;
        Long cuocTroChuyenId = tinNhan.getCuocTroChuyen() != null ? tinNhan.getCuocTroChuyen().getId() : null;
        return new TinNhanDTO(
                tinNhan.getId(),
                cuocTroChuyenId,
                nguoiGuiId,
                tenNguoiGui,
                tinNhan.getNoiDung(),
                tinNhan.getThoiGianGui(),
                Boolean.TRUE.equals(tinNhan.getDaDoc()),
                nguoiGuiId != null && nguoiGuiId.equals(idNguoiXem)
        );
    }
}
