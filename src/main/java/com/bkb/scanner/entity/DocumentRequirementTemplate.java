package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "csob_document_requirement_templates")
@Data
@EqualsAndHashCode(callSuper = false)
public class DocumentRequirementTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean required = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer validityMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementType requirementType;

    @Enumerated(EnumType.STRING)
    private RequirementCategory category;

    // For entity-specific requirements
    @Enumerated(EnumType.STRING)
    private EntityData.EntityType entityType;

    // For individual requirements
    private String residencyStatus;

    // For risk-based requirements
    @Enumerated(EnumType.STRING)
    private Case.RiskLevel riskLevel;

    // For account-specific forms
    private String accountType;

    private Integer sortOrder = 0;

    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public enum RequirementType {
        ENTITY_DOCUMENT,      // Company/entity specific docs
        INDIVIDUAL_DOCUMENT,  // Personal docs based on residency
        BANK_FORM,           // Standard bank forms
        RISK_BASED,          // Additional docs based on risk
        ACCOUNT_FORM         // Account-specific forms
    }

    public enum RequirementCategory {
        IDENTITY,
        CORPORATE,
        FINANCIAL,
        LEGAL,
        COMPLIANCE,
        ACCOUNT_OPENING,
        OTHER
    }
}