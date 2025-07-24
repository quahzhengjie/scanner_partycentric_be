package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "csob_account_types")
@Data
public class AccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // e.g., "CURRENT", "SAVINGS"

    @Column(nullable = false)
    private String displayName; // e.g., "Current Account", "Savings Account"

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer sortOrder = 0;

    private Boolean isActive = true;

    // Features that can be enabled for this account type
    private Boolean allowOnlineBanking = true;
    private Boolean allowCheckBook = true;
    private Boolean allowDebitCard = true;
    private Boolean allowForeignCurrency = true;

    // Minimum requirements
    private BigDecimal minInitialDeposit;
    private BigDecimal minBalance;

    // Document requirements (JSON or delimited string)
    @Column(columnDefinition = "TEXT")
    private String requiredDocuments;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}