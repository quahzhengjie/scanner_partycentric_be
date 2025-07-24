package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "csob_verified_submission_records")
@Data
@EqualsAndHashCode(callSuper = false)
public class VerifiedSubmissionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id", nullable = false)
    @ToString.Exclude
    private ApprovalSnapshot snapshot;

    @Column(nullable = false)
    private String requirementId;

    @Column(nullable = false)
    private String docType;

    @Column(nullable = false)
    private String submissionId;

    @Column(nullable = false)
    private String masterDocId;

    @Column(nullable = false)
    private LocalDateTime verifiedDate;

    private LocalDate expiryDate;
}