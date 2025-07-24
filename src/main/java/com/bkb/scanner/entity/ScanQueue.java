package com.bkb.scanner.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "csob_scan_queue")
@Data
public class ScanQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String scanId;

    @Column(nullable = false)
    private String caseId;

    @Column(nullable = false)
    private String requirementId;

    @Column(nullable = false)
    private String docType;

    @Column(nullable = false)
    private String partyId;

    private String scanProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScanStatus status = ScanStatus.PENDING;

    private Integer progress = 0;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private String documentId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public enum ScanStatus {
        PENDING, SCANNING, PROCESSING, COMPLETED, FAILED
    }
}