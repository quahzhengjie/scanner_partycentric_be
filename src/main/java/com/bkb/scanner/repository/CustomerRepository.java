// CustomerRepository.java
package com.bkb.scanner.repository;

import com.bkb.scanner.entity.Customer;
import com.bkb.scanner.entity.CustomerLifecycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByBasicNumber(String basicNumber);

    List<Customer> findByBasicNumberContainingIgnoreCase(String basicNumber);

    List<Customer> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Customer c WHERE " +
            "(:basicNumber IS NULL OR LOWER(c.basicNumber) LIKE LOWER(CONCAT('%', :basicNumber, '%'))) AND " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lifecycleStatus IS NULL OR c.lifecycleStatus = :lifecycleStatus)")
    List<Customer> searchCustomers(
            @Param("basicNumber") String basicNumber,
            @Param("name") String name,
            @Param("lifecycleStatus") CustomerLifecycleStatus lifecycleStatus);

    List<Customer> findByLifecycleStatus(CustomerLifecycleStatus status);

    @Query("SELECT c.lifecycleStatus as status, COUNT(c) as count FROM Customer c GROUP BY c.lifecycleStatus")
    List<Map<String, Object>> getLifecycleStatusCounts();
}