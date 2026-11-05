package com.carehealth.repository;
import com.carehealth.model.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AccountRepository extends JpaRepository<Account,Long> {
 Optional<Account> findByPhone(String phone);
 Optional<Account> findByEmailIgnoreCase(String email);
 boolean existsByPhone(String phone);
 boolean existsByEmailIgnoreCase(String email);
}
