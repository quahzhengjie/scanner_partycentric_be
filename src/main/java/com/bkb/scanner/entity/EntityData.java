package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Embeddable
@Data
public class EntityData {
    private String entityName;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private String registrationNumber;
    private String taxId;
    private String legalForm;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1", column = @Column(name = "registered_address_line1")),
            @AttributeOverride(name = "line2", column = @Column(name = "registered_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "registered_address_city")),
            @AttributeOverride(name = "state", column = @Column(name = "registered_address_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "registered_address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "registered_address_country"))
    })
    private Address registeredAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1", column = @Column(name = "operating_address_line1")),
            @AttributeOverride(name = "line2", column = @Column(name = "operating_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "operating_address_city")),
            @AttributeOverride(name = "state", column = @Column(name = "operating_address_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "operating_address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "operating_address_country"))
    })
    private Address operatingAddress;

    private String website;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;

    // Business Information
    private LocalDate incorporationDate;
    private String incorporationCountry;

    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Column(columnDefinition = "TEXT")
    private String businessDescription;

    private String numberOfEmployees;
    private String annualRevenue;

    // Ownership
    @Column(columnDefinition = "TEXT")
    private String ownershipStructure;
    private String parentCompany;

    private Boolean isPubliclyTraded;
    private String stockExchange;
    private String stockSymbol;

    public enum EntityType {
        INDIVIDUAL_ACCOUNT, NON_LISTED_COMPANY, LISTED_COMPANY,
        PARTNERSHIP, TRUST, SOCIETY_ASSOCIATION_CLUB, CHARITY,
        SOLE_PROPRIETORSHIP, GOVERNMENT_ENTITY, FINANCIAL_INSTITUTION
    }

    public enum Industry {
        BANKING_FINANCIAL_SERVICES, TECHNOLOGY, HEALTHCARE,
        MANUFACTURING, RETAIL_ECOMMERCE, REAL_ESTATE,
        ENERGY_UTILITIES, TELECOMMUNICATIONS, TRANSPORTATION_LOGISTICS,
        EDUCATION, ENTERTAINMENT_MEDIA, AGRICULTURE,
        MINING_RESOURCES, PROFESSIONAL_SERVICES, NON_PROFIT,
        GOVERNMENT, OTHER
    }
}