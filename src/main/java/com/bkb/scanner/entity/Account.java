package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "available_balance", nullable = false)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @Column(name = "last_transaction_date")
    private LocalDate lastTransactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "is_joint", nullable = false)
    private Boolean isJoint = false;

    // Joint holders as a comma-separated list
    @Column(name = "joint_holders")
    private String jointHolders;

    @Column(name = "account_manager")
    private String accountManager;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "interest_rate", nullable = false)
    private Double interestRate = 0.0;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    // Account features as a JSON string or as separate boolean fields
    @Column(name = "internet_banking_enabled")
    private Boolean internetBankingEnabled = false;

    @Column(name = "mobile_app_enabled")
    private Boolean mobileAppEnabled = false;

    @Column(name = "debit_card_enabled")
    private Boolean debitCardEnabled = false;

    @Column(name = "checkbook_enabled")
    private Boolean checkbookEnabled = false;

    @Column(name = "standing_instructions_enabled")
    private Boolean standingInstructionsEnabled = false;

    @Column(name = "direct_debit_enabled")
    private Boolean directDebitEnabled = false;

    // Relationship with account documents
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountDocument> documents = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.openingDate == null) {
            this.openingDate = LocalDate.now();
        }
        if (this.lastTransactionDate == null) {
            this.lastTransactionDate = this.openingDate;
        }
    }
}