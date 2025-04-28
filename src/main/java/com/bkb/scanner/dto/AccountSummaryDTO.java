// AccountSummaryDTO.java
package com.bkb.scanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummaryDTO {
    private long totalAccounts;
    private long activeAccounts;
    private long pendingAccounts;
    private long frozenAccounts;
    private long dormantAccounts;
    private long closedAccounts;
    private Map<String, BigDecimal> balancesByCurrency;
    private Map<String, Long> accountTypeDistribution;
}