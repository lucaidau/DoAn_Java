package com.example.demo.repository;
import com.example.demo.entity.BacSi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.*;
public interface BacSiRepository extends JpaRepository<BacSi, Long> {
}
