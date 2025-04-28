// AccountTypeDetailsDTO.java
package com.bkb.scanner.dto;

import com.bkb.scanner.entity.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTypeDetailsDTO {
    private String name;
    private String description;
    private BigDecimal minimumBalance;
    private BigDecimal maintenanceFee;
    private Double interestRate;
    private Boolean isOfferedToIndividuals;
    private Boolean isOfferedToCorporates;
    private List<CurrencyCode> currenciesSupported;
    private List<AccountRequirementDTO> documentRequirements;
    private List<String> features;
}