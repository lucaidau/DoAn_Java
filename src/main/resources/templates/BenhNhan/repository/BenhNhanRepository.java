package com.example.baitapluyen.repository;

import com.example.baitapluyen.model.BenhNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenhNhanRepository extends JpaRepository<BenhNhan, Long> {
}
