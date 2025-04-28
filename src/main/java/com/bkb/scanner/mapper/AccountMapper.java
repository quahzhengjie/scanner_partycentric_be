// AccountMapper.java
package com.bkb.scanner.mapper;

import com.bkb.scanner.dto.AccountDTO;
import com.bkb.scanner.dto.AccountDocumentDTO;
import com.bkb.scanner.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    private final AccountDocumentMapper accountDocumentMapper;

    @Autowired
    public AccountMapper(AccountDocumentMapper accountDocumentMapper) {
        this.accountDocumentMapper = accountDocumentMapper;
    }

    public AccountDTO toDTO(Account entity) {
        if (entity == null) {
            return null;
        }

        // Parse joint holders from comma-separated string
        List<String> jointHoldersList = new ArrayList<>();
        if (entity.getJointHolders() != null && !entity.getJointHolders().isEmpty()) {
            jointHoldersList = Arrays.asList(entity.getJointHolders().split(","));
        }

        // Map documents
        List<AccountDocumentDTO> documentDTOs = entity.getDocuments().stream()
                .map(accountDocumentMapper::toDTO)
                .collect(Collectors.toList());

        return AccountDTO.builder()
                .accountNumber(entity.getAccountNumber())
                .accountName(entity.getAccountName())
                .accountType(entity.getAccountType())
                .status(entity.getStatus())
                .currency(entity.getCurrency())
                .balance(entity.getBalance())
                .availableBalance(entity.getAvailableBalance())
                .openingDate(entity.getOpeningDate())
                .lastTransactionDate(entity.getLastTransactionDate())
                .customerBasicNumber(entity.getCustomer().getBasicNumber())
                .isJoint(entity.getIsJoint())
                .jointHolders(jointHoldersList)
                .accountManager(entity.getAccountManager())
                .branchCode(entity.getBranchCode())
                .interestRate(entity.getInterestRate())
                .maturityDate(entity.getMaturityDate())
                .internetBankingEnabled(entity.getInternetBankingEnabled())
                .mobileAppEnabled(entity.getMobileAppEnabled())
                .debitCardEnabled(entity.getDebitCardEnabled())
                .checkbookEnabled(entity.getCheckbookEnabled())
                .standingInstructionsEnabled(entity.getStandingInstructionsEnabled())
                .directDebitEnabled(entity.getDirectDebitEnabled())
                .documents(documentDTOs)
                .build();
    }
}