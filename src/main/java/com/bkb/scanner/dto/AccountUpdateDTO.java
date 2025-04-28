// AccountUpdateDTO.java
package com.bkb.scanner.dto;

import com.bkb.scanner.entity.AccountStatus;
import lombok.Data;

import java.util.List;

@Data
public class AccountUpdateDTO {
    private String accountName;
    private AccountStatus status;
    private Double interestRate;
    private String maturityDate;
    private Boolean internetBankingEnabled;
    private Boolean mobileAppEnabled;
    private Boolean debitCardEnabled;
    private Boolean checkbookEnabled;
    private Boolean standingInstructionsEnabled;
    private Boolean directDebitEnabled;
    private Boolean isJoint;
    private List<String> jointHolders;
    private String accountManager;
    private String branchCode;
}