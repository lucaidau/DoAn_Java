package com.example.baitapluyen.repository;

import com.example.baitapluyen.model.BacSi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BacSiRepository extends JpaRepository<BacSi, Long> {
}
