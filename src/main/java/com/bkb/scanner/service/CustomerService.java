// CustomerService.java
package com.bkb.scanner.service;

import com.bkb.scanner.dto.CustomerDTO;
import com.bkb.scanner.dto.LifecycleStatusCountDTO;
import com.bkb.scanner.dto.SelectOptionDTO;
import com.bkb.scanner.entity.CustomerLifecycleStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();

    Optional<CustomerDTO> getCustomerByBasicNumber(String basicNumber);

    CustomerDTO addCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    List<CustomerDTO> searchCustomers(String basicNumber, String name, CustomerLifecycleStatus lifecycleStatus);

    CustomerDTO updateLifecycleStatus(String basicNumber, CustomerLifecycleStatus newStatus);

    List<CustomerDTO> getCustomersByLifecycleStatus(CustomerLifecycleStatus status);

    Map<CustomerLifecycleStatus, Long> getLifecycleStatusCounts();

    List<SelectOptionDTO> getLifecycleStatusOptions();
}