// AccountController.java
package com.bkb.scanner.controller;

import com.bkb.scanner.dto.*;
import com.bkb.scanner.entity.AccountStatus;
import com.bkb.scanner.entity.AccountType;
import com.bkb.scanner.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@CrossOrigin
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccountByNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{basicNumber}")
    public ResponseEntity<List<AccountDTO>> getAccountsByCustomerNumber(@PathVariable String basicNumber) {
        List<AccountDTO> accounts = accountService.getAccountsByCustomerNumber(basicNumber);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/type-details/{accountType}")
    public ResponseEntity<AccountTypeDetailsDTO> getAccountTypeDetails(@PathVariable AccountType accountType) {
        AccountTypeDetailsDTO details = accountService.getAccountTypeDetails(accountType);
        if (details == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }

    @GetMapping("/type-options")
    public ResponseEntity<List<SelectOptionDTO>> getAccountTypeOptions() {
        return ResponseEntity.ok(accountService.getAccountTypeOptions());
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountCreateDTO accountDTO) {
        AccountDTO newAccount = accountService.createAccount(accountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    }

    @PutMapping("/{accountNumber}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable String accountNumber,
            @RequestBody AccountUpdateDTO updates) {

        AccountDTO updatedAccount = accountService.updateAccount(accountNumber, updates);
        return ResponseEntity.ok(updatedAccount);
    }

    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<AccountDTO> changeAccountStatus(
            @PathVariable String accountNumber,
            @RequestParam AccountStatus status) {

        AccountDTO updatedAccount = accountService.changeAccountStatus(accountNumber, status);
        return ResponseEntity.ok(updatedAccount);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccountDTO>> getAccountsByStatus(@PathVariable AccountStatus status) {
        List<AccountDTO> accounts = accountService.getAccountsByStatus(status);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/{accountNumber}/documents/{documentName}")
    public ResponseEntity<UploadResultDTO> updateAccountDocument(
            @PathVariable String accountNumber,
            @PathVariable String documentName,
            @RequestParam("file") MultipartFile file) {

        UploadResultDTO result = accountService.updateAccountDocument(accountNumber, documentName, file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{accountNumber}/documents/validate")
    public ResponseEntity<Boolean> hasAllRequiredDocuments(@PathVariable String accountNumber) {
        boolean hasAll = accountService.hasAllRequiredDocuments(accountNumber);
        return ResponseEntity.ok(hasAll);
    }

    @GetMapping("/{accountNumber}/documents/missing")
    public ResponseEntity<List<String>> getMissingDocuments(@PathVariable String accountNumber) {
        List<String> missingDocs = accountService.getMissingDocuments(accountNumber);
        return ResponseEntity.ok(missingDocs);
    }

    @GetMapping("/summary")
    public ResponseEntity<AccountSummaryDTO> getAccountSummary() {
        return ResponseEntity.ok(accountService.getAccountSummary());
    }
}