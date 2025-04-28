// AccountDTO.java
package com.bkb.scanner.dto;

import com.bkb.scanner.entity.AccountStatus;
import com.bkb.scanner.entity.AccountType;
import com.bkb.scanner.entity.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private String accountNumber;
    private String accountName;
    private AccountType accountType;
    private AccountStatus status;
    private CurrencyCode currency;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private LocalDate openingDate;
    private LocalDate lastTransactionDate;
    private String customerBasicNumber;
    private Boolean isJoint;
    private List<String> jointHolders;
    private String accountManager;
    private String branchCode;
    private Double interestRate;
    private LocalDate maturityDate;

    // Account features
    private Boolean internetBankingEnabled;
    private Boolean mobileAppEnabled;
    private Boolean debitCardEnabled;
    private Boolean checkbookEnabled;
    private Boolean standingInstructionsEnabled;
    private Boolean directDebitEnabled;

    // Document information
    private List<AccountDocumentDTO> documents;
}