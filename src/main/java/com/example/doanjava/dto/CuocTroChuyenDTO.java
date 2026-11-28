package com.example.doanjava.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CuocTroChuyenDTO {
    private Long cuocId;
    private Long doiPhuongId;
    private String tenDoiPhuong;
    private String vaiTroDoiPhuong;     // "Bác sĩ", "Bệnh nhân", "Admin"
    private String tinCuoi;             // null nếu chưa có tin nào
    private LocalDateTime thoiGianCuoi; // thời gian tin cuối (hoặc ngày tạo cuộc trò chuyện)
    private long soChuaDoc;
}
