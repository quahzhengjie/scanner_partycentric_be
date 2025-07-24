package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "csob_activity_logs")
@Data
@EqualsAndHashCode(callSuper = false)
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @ToString.Exclude
    private Case caseEntity;  // Using caseEntity instead of case

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Actor info
    @Column(nullable = false)
    private String actor;

    private String actorRole;

    private String actorId;

    // Action details
    @Column(nullable = false)
    private String action;

    private String actionType;

    private String entityType;

    private String entityId;

    // Change details
    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(columnDefinition = "TEXT")
    private String previousValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    // Metadata
    private String ipAddress;

    private String userAgent;

    private String sessionId;
}