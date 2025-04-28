package com.bkb.scanner.dto;

import com.bkb.scanner.entity.AmlStatus;
import com.bkb.scanner.entity.CustomerLifecycleStatus;
import com.bkb.scanner.entity.CustomerType;
import com.bkb.scanner.entity.RiskRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private String basicNumber;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private String passport;
    private CustomerType customerType;
    private String incorporationNumber;
    private String registrationCountry;
    private List<String> ubos; // Ultimate Beneficial Owners
    private Boolean isPEP; // Politically Exposed Person
    private AmlStatus amlStatus;
    private LocalDate amlCheckDate;
    private Integer amlScore;
    private String amlNotes;
    private RiskRating riskRating;
    private CustomerLifecycleStatus lifecycleStatus;
    private LocalDate lifecycleStatusDate;
    private String relationshipManager;
    private Boolean hasOutstandingDocuments;

    // New fields for enhanced customer data
    private LocalDate dateOfBirth;
    private String nationality;
    private String primaryContact;
    private String businessNature;
}