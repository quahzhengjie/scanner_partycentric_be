package com.bkb.scanner.repository;

import com.bkb.scanner.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
    Optional<AccountType> findByCode(String code);

    List<AccountType> findByIsActiveTrueOrderBySortOrder();

    boolean existsByCodeAndIsActiveTrue(String code);

    List<AccountType> findByIsActiveTrue();
}