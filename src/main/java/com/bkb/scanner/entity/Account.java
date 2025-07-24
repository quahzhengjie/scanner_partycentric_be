package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "csob_accounts")
@Data
@EqualsAndHashCode(callSuper = false)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @ToString.Exclude
    private Case caseEntity;  // Using caseEntity instead of case

    private String accountNumber;

    // Changed from enum to String
    @Column(nullable = false)
    private String accountType;

    // Changed from enum to String
    @Column(nullable = false)
    private String status = "PROPOSED";

    private String currency = "SGD";

    private String purpose;

    // Financials
    private String expectedMonthlyCredits;
    private String expectedMonthlyDebits;
    private String initialDeposit;

    // Parties
    private String primaryHolderId;
    private String signatureRules;

    // Features
    private Boolean onlineBanking = false;
    private Boolean checkBook = false;
    private Boolean debitCard = false;

    // Relationships
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<AccountSignatory> signatories = new ArrayList<>();

    // Activation
    private Long accountApprovalSnapshotId;
    private LocalDateTime activatedDate;
    private String activatedBy;

    // Metadata
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private LocalDate lastReviewDate;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PROPOSED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Add a signatory to this account
     */
    public void addSignatory(AccountSignatory signatory) {
        signatories.add(signatory);
        signatory.setAccount(this);
    }

    /**
     * Remove a signatory from this account
     */
    public void removeSignatory(AccountSignatory signatory) {
        signatories.remove(signatory);
        signatory.setAccount(null);
    }

    /**
     * Get all active signatories
     */
    public List<AccountSignatory> getActiveSignatories() {
        return signatories.stream()
                .filter(s -> s.getIsActive() != null && s.getIsActive())
                .collect(Collectors.toList());
    }

    /**
     * Check if account is in an active state
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    /**
     * Check if account can be activated
     */
    public boolean canBeActivated() {
        return "PENDING_COMPLIANCE_REVIEW".equals(status) &&
                accountNumber != null && !accountNumber.isEmpty();
    }

    /**
     * Activate the account
     */
    public void activate(String activatedBy, String accountNumber) {
        if (!canBeActivated()) {
            throw new IllegalStateException("Account cannot be activated in current state");
        }
        this.status = "ACTIVE";
        this.activatedDate = LocalDateTime.now();
        this.activatedBy = activatedBy;
        this.accountNumber = accountNumber;
    }
}