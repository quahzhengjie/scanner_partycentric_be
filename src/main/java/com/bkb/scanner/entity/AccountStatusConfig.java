package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "csob_account_status_config")
@Data
public class AccountStatusConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // e.g., "PROPOSED", "ACTIVE"

    @Column(nullable = false)
    private String displayName; // e.g., "Proposed", "Active"

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer sortOrder = 0;

    private Boolean isActive = true;

    // Workflow configuration
    @Column(name = "next_statuses", columnDefinition = "TEXT")
    private String nextStatuses; // Comma-separated list of allowed next statuses

    @Column(name = "required_role")
    private String requiredRole; // Role required to move to this status

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}