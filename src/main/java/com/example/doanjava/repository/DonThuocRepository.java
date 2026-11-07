package com.example.demo.repository;
import com.example.demo.entity.DonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.*;
public interface DonThuocRepository extends JpaRepository<DonThuoc, Long> {
    List<DonThuoc> findByHoSoBenhAnId(Long id);
    void deleteByHoSoBenhAnId(Long id);
}
