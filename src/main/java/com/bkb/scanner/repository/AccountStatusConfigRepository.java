package com.bkb.scanner.repository;

import com.bkb.scanner.entity.AccountStatusConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountStatusConfigRepository extends JpaRepository<AccountStatusConfig, Long> {
    Optional<AccountStatusConfig> findByCode(String code);

    List<AccountStatusConfig> findByIsActiveTrueOrderBySortOrder();

    boolean existsByCodeAndIsActiveTrue(String code);

    List<AccountStatusConfig> findByIsActiveTrue();
}