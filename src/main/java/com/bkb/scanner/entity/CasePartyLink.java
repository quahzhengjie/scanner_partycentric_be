package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "csob_case_party_links")
@Data
@EqualsAndHashCode(callSuper = false)
public class CasePartyLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @ToString.Exclude
    private Case caseEntity;  // Using caseEntity instead of case

    // Changed from @ManyToOne reference to simple String
    @Column(name = "party_id", nullable = false)
    private String partyId;

    @Column(nullable = false)
    private String relationshipType;

    private BigDecimal ownershipPercentage;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isPrimary = false;
}