package com.bkb.scanner.repository;

import com.bkb.scanner.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(String accountId);
    List<Account> findByCaseEntity_CaseId(String caseId);
    List<Account> findByStatus(String status);
    boolean existsByAccountId(String accountId);
}