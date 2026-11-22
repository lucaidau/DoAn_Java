package com.example.doanjava.dto.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaoCaDTO {
    private Long bacSiId;
    private Long phongKhamId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate tuNgay;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate denNgay;

    // Các thứ trong tuần: 1 = Thứ 2 ... 7 = Chủ nhật (theo DayOfWeek của Java)
    private List<Integer> thu = new ArrayList<>();

    // "SANG", "CHIEU"
    private List<String> caLam = new ArrayList<>();

    private Integer soLuongToiDa = 10;
}