package com.bkb.scanner.service.impl;

import com.bkb.scanner.dto.CustomerDTO;
import com.bkb.scanner.dto.SelectOptionDTO;
import com.bkb.scanner.entity.Customer;
import com.bkb.scanner.entity.CustomerLifecycleStatus;
import com.bkb.scanner.mapper.CustomerMapper;
import com.bkb.scanner.repository.CustomerRepository;
import com.bkb.scanner.service.CustomerService;
import com.bkb.scanner.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final DocumentService documentService;

    @Autowired
    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            DocumentService documentService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.documentService = documentService;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerByBasicNumber(String basicNumber) {
        return customerRepository.findByBasicNumber(basicNumber)
                .map(customerMapper::toDTO);
    }

    @Override
    @Transactional
    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        // Generate new basic number
        String lastBasicNumber = customerRepository.findAll().stream()
                .map(Customer::getBasicNumber)
                .filter(bn -> bn.startsWith("BN"))
                .map(bn -> bn.replace("BN", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0) + "";

        String newBasicNumber = "BN" + String.format("%03d", Integer.parseInt(lastBasicNumber) + 1);

        customerDTO.setBasicNumber(newBasicNumber);

        // Set default lifecycle status as "Onboarding" for new customers if not provided
        if (customerDTO.getLifecycleStatus() == null) {
            customerDTO.setLifecycleStatus(CustomerLifecycleStatus.Onboarding);
        }

        // Set lifecycle status date if not provided
        if (customerDTO.getLifecycleStatusDate() == null) {
            customerDTO.setLifecycleStatusDate(LocalDate.now());
        }

        // Process new fields based on customer type
        if (customerDTO.getCustomerType() != null) {
            if (customerDTO.getCustomerType().equals("Individual")) {
                // Individual-specific processing
                // No special processing needed at the moment
            } else {
                // Corporate-specific processing
                // No special processing needed at the moment
            }
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);

        // Initialize document records for this new customer
        documentService.initializeCustomerDocuments(
                savedCustomer.getBasicNumber(),
                savedCustomer.getCustomerType(),
                savedCustomer.getIsPEP(),
                savedCustomer.getRegistrationCountry(),
                savedCustomer.getRiskRating()
        );

        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findByBasicNumber(customerDTO.getBasicNumber())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Update customer entity with DTO values
        customerMapper.updateEntityFromDTO(customerDTO, existingCustomer);
        Customer savedCustomer = customerRepository.save(existingCustomer);

        // Update documents if customer type or risk profile has changed
        if (!existingCustomer.getCustomerType().equals(savedCustomer.getCustomerType()) ||
                !existingCustomer.getRiskRating().equals(savedCustomer.getRiskRating()) ||
                !existingCustomer.getIsPEP().equals(savedCustomer.getIsPEP()) ||
                !existingCustomer.getRegistrationCountry().equals(savedCustomer.getRegistrationCountry())) {

            documentService.updateCustomerDocuments(
                    savedCustomer.getBasicNumber(),
                    savedCustomer.getCustomerType(),
                    savedCustomer.getIsPEP(),
                    savedCustomer.getRegistrationCountry(),
                    savedCustomer.getRiskRating()
            );
        }

        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    public List<CustomerDTO> searchCustomers(String basicNumber, String name, CustomerLifecycleStatus lifecycleStatus) {
        List<Customer> customers;

        if (basicNumber != null && !basicNumber.isEmpty() && name != null && !name.isEmpty()) {
            // Search by both basicNumber and name
            customers = customerRepository.findByBasicNumberContainingIgnoreCase(basicNumber).stream()
                    .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (basicNumber != null && !basicNumber.isEmpty()) {
            // Search by basicNumber only
            customers = customerRepository.findByBasicNumberContainingIgnoreCase(basicNumber);
        } else if (name != null && !name.isEmpty()) {
            // Search by name only
            customers = customerRepository.findByNameContainingIgnoreCase(name);
        } else {
            // No search criteria, return all
            customers = customerRepository.findAll();
        }

        // Filter by lifecycle status if provided
        if (lifecycleStatus != null) {
            customers = customers.stream()
                    .filter(c -> c.getLifecycleStatus() == lifecycleStatus)
                    .collect(Collectors.toList());
        }

        // Enrich with document status
        List<CustomerDTO> customerDTOs = customers.stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());

        for (CustomerDTO dto : customerDTOs) {
            boolean hasOutstanding = documentService.hasOutstandingDocuments(dto.getBasicNumber());
            dto.setHasOutstandingDocuments(hasOutstanding);
        }

        return customerDTOs;
    }

    @Override
    @Transactional
    public CustomerDTO updateLifecycleStatus(String basicNumber, CustomerLifecycleStatus newStatus) {
        Customer customer = customerRepository.findByBasicNumber(basicNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setLifecycleStatus(newStatus);
        customer.setLifecycleStatusDate(LocalDate.now());

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    public List<CustomerDTO> getCustomersByLifecycleStatus(CustomerLifecycleStatus status) {
        List<Customer> customers = customerRepository.findByLifecycleStatus(status);
        return customers.stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<CustomerLifecycleStatus, Long> getLifecycleStatusCounts() {
        Map<CustomerLifecycleStatus, Long> counts = new HashMap<>();

        // Initialize all statuses with 0 count
        for (CustomerLifecycleStatus status : CustomerLifecycleStatus.values()) {
            counts.put(status, 0L);
        }

        // Update counts from database
        List<Map<String, Object>> rawCounts = customerRepository.getLifecycleStatusCounts();
        for (Map<String, Object> entry : rawCounts) {
            CustomerLifecycleStatus status = (CustomerLifecycleStatus) entry.get("status");
            Long count = ((Number) entry.get("count")).longValue();
            counts.put(status, count);
        }

        return counts;
    }

    @Override
    public List<SelectOptionDTO> getLifecycleStatusOptions() {
        return Arrays.stream(CustomerLifecycleStatus.values())
                .map(status -> new SelectOptionDTO(status.name(), status.name()))
                .collect(Collectors.toList());
    }
}