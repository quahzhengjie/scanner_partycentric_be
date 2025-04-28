// AccountService.java
package com.bkb.scanner.service;

import com.bkb.scanner.dto.*;
import com.bkb.scanner.entity.AccountStatus;
import com.bkb.scanner.entity.AccountType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountService {

    List<AccountDTO> getAllAccounts();

    Optional<AccountDTO> getAccountByNumber(String accountNumber);

    List<AccountDTO> getAccountsByCustomerNumber(String basicNumber);

    AccountTypeDetailsDTO getAccountTypeDetails(AccountType accountType);

    List<SelectOptionDTO> getAccountTypeOptions();

    AccountDTO createAccount(AccountCreateDTO accountDTO);

    AccountDTO updateAccount(String accountNumber, AccountUpdateDTO updates);

    AccountDTO changeAccountStatus(String accountNumber, AccountStatus newStatus);

    List<AccountDTO> getAccountsByStatus(AccountStatus status);

    UploadResultDTO updateAccountDocument(
            String accountNumber,
            String documentName,
            MultipartFile file);

    boolean hasAllRequiredDocuments(String accountNumber);

    List<String> getMissingDocuments(String accountNumber);

    AccountSummaryDTO getAccountSummary();
}
