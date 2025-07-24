// 4. ConfigurationService.java - Service to manage configurations
package com.bkb.scanner.service;

import com.bkb.scanner.entity.AccountType;
import com.bkb.scanner.entity.AccountStatusConfig;
import com.bkb.scanner.repository.AccountTypeRepository;
import com.bkb.scanner.repository.AccountStatusConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigurationService {
    private final AccountTypeRepository accountTypeRepository;
    private final AccountStatusConfigRepository accountStatusConfigRepository;

    @Cacheable("accountTypes")
    public List<AccountType> getActiveAccountTypes() {
        return accountTypeRepository.findByIsActiveTrueOrderBySortOrder();
    }

    @Cacheable("accountTypesMap")
    public Map<String, AccountType> getAccountTypesMap() {
        return getActiveAccountTypes().stream()
                .collect(Collectors.toMap(AccountType::getCode, t -> t));
    }

    @Cacheable("accountStatuses")
    public List<AccountStatusConfig> getActiveAccountStatuses() {
        return accountStatusConfigRepository.findByIsActiveTrueOrderBySortOrder();
    }

    @Cacheable("accountStatusesMap")
    public Map<String, AccountStatusConfig> getAccountStatusesMap() {
        return getActiveAccountStatuses().stream()
                .collect(Collectors.toMap(AccountStatusConfig::getCode, s -> s));
    }

    public boolean isValidAccountType(String code) {
        return accountTypeRepository.existsByCodeAndIsActiveTrue(code);
    }

    public boolean isValidAccountStatus(String code) {
        return accountStatusConfigRepository.existsByCodeAndIsActiveTrue(code);
    }

    public List<String> getNextAllowedStatuses(String currentStatus) {
        return accountStatusConfigRepository.findByCode(currentStatus)
                .map(config -> {
                    String nextStatuses = config.getNextStatuses();
                    if (nextStatuses == null || nextStatuses.isEmpty()) {
                        return List.<String>of();
                    }
                    return List.of(nextStatuses.split(","));
                })
                .orElse(List.of());
    }
}