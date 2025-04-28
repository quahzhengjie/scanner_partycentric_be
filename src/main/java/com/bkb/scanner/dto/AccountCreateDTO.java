// AccountCreateDTO.java
package com.bkb.scanner.dto;

import com.bkb.scanner.entity.AccountType;
import com.bkb.scanner.entity.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDTO {
    private String accountName;
    private AccountType accountType;
    private CurrencyCode currency;
    private String customerBasicNumber;
    private Boolean isJoint;
    private List<String> jointHolders;
}