// AccountRepository.java
package com.bkb.scanner.repository;

import com.bkb.scanner.entity.Account;
import com.bkb.scanner.entity.AccountStatus;
import com.bkb.scanner.entity.AccountType;
import com.bkb.scanner.entity.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByCustomerBasicNumber(String basicNumber);

    @Query("SELECT a FROM Account a WHERE a.customer.basicNumber = :basicNumber OR " +
            "(a.isJoint = true AND a.jointHolders LIKE CONCAT('%', :basicNumber, '%'))")
    List<Account> findAccountsByCustomerBasicNumber(@Param("basicNumber") String basicNumber);

    List<Account> findByStatus(AccountStatus status);

    @Query("SELECT a.currency as currency, SUM(a.balance) as totalBalance " +
            "FROM Account a WHERE a.status <> 'Closed' GROUP BY a.currency")
    List<Map<String, Object>> getTotalBalancesByCurrency();

    @Query("SELECT a.accountType as type, COUNT(a) as count FROM Account a GROUP BY a.accountType")
    List<Map<String, Object>> getAccountTypeDistribution();
}