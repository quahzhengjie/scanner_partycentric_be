package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "csob_documents")
@Data
@EqualsAndHashCode(callSuper = false)
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String docId;

    // Changed from @ManyToOne reference to simple String
    @Column(name = "owner_party_id", nullable = false)
    private String ownerPartyId;

    @Column(nullable = false)
    private String docType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentCategory category;

    // File info
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String fileUrl;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileData;

    // Document details
    private String issuer;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String documentNumber;

    // Verification
    private Boolean isVerified = false;
    private String verifiedBy;
    private LocalDateTime verifiedDate;

    @Column(columnDefinition = "TEXT")
    private String verificationNotes;

    // Metadata
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    private String uploadedBy;
    private LocalDateTime lastAccessedAt;

    public enum DocumentCategory {
        IDENTITY, ADDRESS, FINANCIAL, CORPORATE, LEGAL, OTHER
    }

    public enum DocumentStatus {
        MISSING, PENDING_CHECKER_VERIFICATION, PENDING_COMPLIANCE_VERIFICATION, VERIFIED, REJECTED
    }

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}