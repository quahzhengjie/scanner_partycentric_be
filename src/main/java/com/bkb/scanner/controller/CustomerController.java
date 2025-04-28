// CustomerController.java
package com.bkb.scanner.controller;

import com.bkb.scanner.dto.CustomerDTO;
import com.bkb.scanner.dto.SelectOptionDTO;
import com.bkb.scanner.entity.CustomerLifecycleStatus;
import com.bkb.scanner.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
@CrossOrigin
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{basicNumber}")
    public ResponseEntity<CustomerDTO> getCustomerByBasicNumber(@PathVariable String basicNumber) {
        return customerService.getCustomerByBasicNumber(basicNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> addCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO newCustomer = customerService.addCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }

    @PutMapping("/{basicNumber}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable String basicNumber,
            @RequestBody CustomerDTO customerDTO) {

        if (!basicNumber.equals(customerDTO.getBasicNumber())) {
            return ResponseEntity.badRequest().build();
        }

        CustomerDTO updatedCustomer = customerService.updateCustomer(customerDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(
            @RequestParam(required = false) String basicNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) CustomerLifecycleStatus lifecycleStatus) {

        List<CustomerDTO> customers = customerService.searchCustomers(basicNumber, name, lifecycleStatus);
        return ResponseEntity.ok(customers);
    }

    @PatchMapping("/{basicNumber}/lifecycle-status")
    public ResponseEntity<CustomerDTO> updateLifecycleStatus(
            @PathVariable String basicNumber,
            @RequestParam CustomerLifecycleStatus status) {

        CustomerDTO updatedCustomer = customerService.updateLifecycleStatus(basicNumber, status);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/lifecycle-status/{status}")
    public ResponseEntity<List<CustomerDTO>> getCustomersByLifecycleStatus(
            @PathVariable CustomerLifecycleStatus status) {

        List<CustomerDTO> customers = customerService.getCustomersByLifecycleStatus(status);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/lifecycle-status-counts")
    public ResponseEntity<Map<CustomerLifecycleStatus, Long>> getLifecycleStatusCounts() {
        return ResponseEntity.ok(customerService.getLifecycleStatusCounts());
    }

    @GetMapping("/lifecycle-status-options")
    public ResponseEntity<List<SelectOptionDTO>> getLifecycleStatusOptions() {
        return ResponseEntity.ok(customerService.getLifecycleStatusOptions());
    }
}