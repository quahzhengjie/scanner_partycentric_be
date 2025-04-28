package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(name = "basic_number", nullable = false)
    private String basicNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private String passport;

    @Column(name = "customer_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @Column(name = "incorporation_number")
    private String incorporationNumber;

    @Column(name = "registration_country")
    private String registrationCountry;

    @ElementCollection
    @CollectionTable(name = "customer_ubos", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "ubo")
    private List<String> ubos = new ArrayList<>();

    @Column(name = "is_pep")
    private Boolean isPEP;

    @Column(name = "aml_status")
    @Enumerated(EnumType.STRING)
    private AmlStatus amlStatus;

    @Column(name = "aml_check_date")
    private LocalDate amlCheckDate;

    @Column(name = "aml_score")
    private Integer amlScore;

    @Column(name = "aml_notes")
    private String amlNotes;

    @Column(name = "risk_rating")
    @Enumerated(EnumType.STRING)
    private RiskRating riskRating;

    @Column(name = "lifecycle_status")
    @Enumerated(EnumType.STRING)
    private CustomerLifecycleStatus lifecycleStatus;

    @Column(name = "lifecycle_status_date")
    private LocalDate lifecycleStatusDate;

    @Column(name = "relationship_manager")
    private String relationshipManager;

    // New fields for enhanced customer data
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column
    private String nationality;

    @Column(name = "primary_contact")
    private String primaryContact;

    @Column(name = "business_nature")
    private String businessNature;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerDocument> documents = new ArrayList<>();

    // For efficient querying
    @Column(name = "has_outstanding_documents")
    private Boolean hasOutstandingDocuments = false;

    @PrePersist
    public void prePersist() {
        if (this.basicNumber == null || this.basicNumber.isEmpty()) {
            // You can implement custom logic for generating basicNumber here
            // For now, it will be set externally
        }

        if (this.lifecycleStatusDate == null && this.lifecycleStatus != null) {
            this.lifecycleStatusDate = LocalDate.now();
        }
    }
}