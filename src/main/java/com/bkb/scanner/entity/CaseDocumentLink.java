package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "csob_case_document_links")
@Data
@EqualsAndHashCode(callSuper = false)
public class CaseDocumentLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String linkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @ToString.Exclude
    private Case caseEntity;  // Changed from 'case' to 'caseEntity'

    @Column(nullable = false)
    private String requirementId;

    private String requirementType = "Standard";

    private Boolean isMandatory = true;

    private LocalDate dueDate;

    @OneToMany(mappedBy = "documentLink", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Submission> submissions = new ArrayList<>();
}