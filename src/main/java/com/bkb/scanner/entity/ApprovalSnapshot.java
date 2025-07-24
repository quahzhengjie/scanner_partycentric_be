package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "csob_approval_snapshots")
@Data
@EqualsAndHashCode(callSuper = false)
public class ApprovalSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String snapshotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @ToString.Exclude
    private Case caseEntity;  // Using caseEntity instead of case

    @Column(nullable = false)
    private String snapshotType;  // KYC, ACCOUNT, etc.

    // Approval details
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String approvedBy;

    private String approverRole;

    @Column(nullable = false)
    private String decision;  // APPROVED, REJECTED

    // Risk & Compliance
    private String riskLevel;

    private Boolean checklistCompleted = false;

    // Validity
    private LocalDate validUntil;

    private Boolean periodicReviewRequired = false;

    private LocalDate nextReviewDate;

    // Document records
    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<VerifiedSubmissionRecord> documents = new ArrayList<>();
}