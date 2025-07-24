package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "csob_account_signatories")
@Data
@EqualsAndHashCode(callSuper = false)
public class AccountSignatory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    private Account account;

    // Changed from @ManyToOne reference to simple String
    @Column(name = "party_id", nullable = false)
    private String partyId;

    @Column(name = "signatory_type")
    private String signatoryType; // e.g., "Primary", "Joint", "Authorized"

    @Column(name = "signature_rule")
    private String signatureRule; // e.g., "Any one", "Any two", "All"

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}