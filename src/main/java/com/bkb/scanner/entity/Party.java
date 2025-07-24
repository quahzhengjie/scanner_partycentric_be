package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "csob_parties")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String partyId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyType type;

    // Individual fields
    private String residencyStatus;
    private String nationality;
    private LocalDate dateOfBirth;
    private String gender;
    private String occupation;
    private String employer;

    // Corporate fields
    private String registrationNumber;
    private LocalDate incorporationDate;
    private String incorporationCountry;
    private String businessType;

    // Contact
    private String email;
    private String phone;
    private String alternatePhone;

    // Address - Embedded object
    @Embedded
    private Address address;

    // Additional address fields for frontend compatibility
    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "address_city")
    private String addressCity;

    @Column(name = "address_state")
    private String addressState;

    @Column(name = "address_postal_code")
    private String addressPostalCode;

    @Column(name = "address_country")
    private String addressCountry;

    // Mailing Address
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1", column = @Column(name = "mailing_address_line1")),
            @AttributeOverride(name = "line2", column = @Column(name = "mailing_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "mailing_address_city")),
            @AttributeOverride(name = "state", column = @Column(name = "mailing_address_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "mailing_address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "mailing_address_country"))
    })
    private Address mailingAddress;

    // Risk & Compliance
    @Column(nullable = false)
    private Boolean isPEP = false;

    @Column(nullable = false)
    private Boolean isSanctioned = false;

    private Integer riskScore;

    // Financial
    private String annualIncome;
    private String netWorth;
    private String sourceOfWealth;
    private String sourceOfFunds;

    // Relationships - Changed to use partyId instead of entity mapping
    // Remove the @JoinColumn and just use:
    @OneToMany(mappedBy = "partyId", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PartyRiskFactor> riskFactors = new ArrayList<>();
    // Metadata
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String createdBy;
    private LocalDate kycRefreshDate;
    private LocalDate nextReviewDate;

    // Enums
    public enum PartyType {
        INDIVIDUAL, CORPORATE
    }

    // Helper methods
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        // Sync address fields if needed
        syncAddressFields();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Sync address fields if needed
        syncAddressFields();
    }

    /**
     * Sync embedded address with separate fields for compatibility
     */
    private void syncAddressFields() {
        if (address != null) {
            if (addressLine1 == null) addressLine1 = address.getLine1();
            if (addressLine2 == null) addressLine2 = address.getLine2();
            if (addressCity == null) addressCity = address.getCity();
            if (addressState == null) addressState = address.getState();
            if (addressPostalCode == null) addressPostalCode = address.getPostalCode();
            if (addressCountry == null) addressCountry = address.getCountry();
        } else if (addressLine1 != null) {
            // Create embedded address from separate fields
            address = new Address();
            address.setLine1(addressLine1);
            address.setLine2(addressLine2);
            address.setCity(addressCity);
            address.setState(addressState);
            address.setPostalCode(addressPostalCode);
            address.setCountry(addressCountry);
        }
    }

    /**
     * Add a risk factor to this party
     */
    public void addRiskFactor(PartyRiskFactor riskFactor) {
        riskFactors.add(riskFactor);
        riskFactor.setPartyId(this.partyId);
    }

    /**
     * Remove a risk factor from this party
     */
    public void removeRiskFactor(PartyRiskFactor riskFactor) {
        riskFactors.remove(riskFactor);
    }

    /**
     * Calculate total risk score from all active risk factors
     */
    public Integer calculateTotalRiskScore() {
        return riskFactors.stream()
                .filter(rf -> rf.getIsActive() != null && rf.getIsActive())
                .mapToInt(rf -> rf.getRiskScore() != null ? rf.getRiskScore() : 0)
                .sum();
    }

    /**
     * Check if party has any high-risk factors
     */
    public boolean hasHighRiskFactors() {
        return isPEP || isSanctioned ||
                (riskScore != null && riskScore > 50) ||
                riskFactors.stream().anyMatch(rf ->
                        rf.getIsActive() && rf.getRiskScore() != null && rf.getRiskScore() > 30
                );
    }
}