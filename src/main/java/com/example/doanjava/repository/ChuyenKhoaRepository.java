package com.example.doanjava.repository;

import com.example.doanjava.entity.ChuyenKhoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.*;
public interface ChuyenKhoaRepository extends JpaRepository<ChuyenKhoa, Long> {
}
