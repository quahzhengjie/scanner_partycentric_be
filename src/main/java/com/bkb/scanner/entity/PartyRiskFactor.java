package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "csob_party_risk_factors")
@Data
@EqualsAndHashCode(callSuper = false)
public class PartyRiskFactor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Change this to use a String reference instead of entity relationship
    @Column(name = "party_id", nullable = false)
    private String partyId;

    @Column(name = "risk_factor", nullable = false)
    private String riskFactor;

    @Column(name = "risk_category")
    @Enumerated(EnumType.STRING)
    private RiskCategory riskCategory;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "identified_date")
    private LocalDateTime identifiedDate;

    @Column(name = "reviewed_date")
    private LocalDateTime reviewedDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public enum RiskCategory {
        GEOGRAPHIC, CUSTOMER, PRODUCT, TRANSACTION, REGULATORY
    }

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