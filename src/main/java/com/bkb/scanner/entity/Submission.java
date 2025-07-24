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
@Table(name = "csob_submissions")
@Data
@EqualsAndHashCode(callSuper = false)
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_link_id", nullable = false)
    @ToString.Exclude
    private CaseDocumentLink documentLink;

    @Column(nullable = false)
    private String masterDocId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    // Submission details
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private String submittedBy;

    @Enumerated(EnumType.STRING)
    private SubmissionMethod submissionMethod;

    // Document metadata
    private LocalDate publishedDate;
    private LocalDate expiryDate;
    private Integer pages;

    // Review process
    private LocalDateTime checkerReviewedAt;
    private String checkerReviewedBy;
    private LocalDateTime complianceReviewedAt;
    private String complianceReviewedBy;

    // Comments
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    // Enums
    public enum DocumentStatus {
        MISSING,
        PENDING_CHECKER_VERIFICATION,
        PENDING_COMPLIANCE_VERIFICATION,
        VERIFIED,
        REJECTED
    }

    public enum SubmissionMethod {
        UPLOAD, SCAN, LINK, MANUAL
    }
}