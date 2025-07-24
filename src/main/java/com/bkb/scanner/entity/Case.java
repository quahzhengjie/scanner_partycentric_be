package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "csob_cases")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String caseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status = CaseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel = RiskLevel.MEDIUM;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    private String assignedTo;
    private String assignedTeam;
    private String checkedBy;
    private String approvedBy;

    @Embedded
    private EntityData entityData;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CasePartyLink> relatedPartyLinks = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CaseDocumentLink> documentLinks = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ActivityLog> activities = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "kyc_approval_snapshot_id")
    private ApprovalSnapshot kycApprovalSnapshot;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ApprovalSnapshot> accountApprovalSnapshots = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String complianceNotes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime targetCompletionDate;
    private LocalDateTime actualCompletionDate;

    // Enums
    public enum CaseStatus {
        DRAFT, PENDING_CHECKER_REVIEW, PENDING_COMPLIANCE_REVIEW,
        PENDING_GM_APPROVAL, APPROVED, REJECTED, ACTIVE
    }

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
}