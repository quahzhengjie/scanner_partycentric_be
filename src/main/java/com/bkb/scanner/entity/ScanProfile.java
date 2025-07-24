package com.bkb.scanner.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "csob_scan_profiles")
@Data
public class ScanProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String profileName;

    private String description;
    private String scannerDevice;
    private String colorMode; // Color, Grayscale, BlackWhite
    private Integer resolution; // DPI
    private String paperSize; // A4, Letter, Legal
    private Boolean duplex = false;
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}