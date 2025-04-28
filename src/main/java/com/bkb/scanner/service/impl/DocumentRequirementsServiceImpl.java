
// DocumentRequirementsServiceImpl.java
package com.bkb.scanner.service.impl;

import com.bkb.scanner.dto.DocumentDTO;
import com.bkb.scanner.dto.SelectOptionDTO;
import com.bkb.scanner.entity.CustomerType;
import com.bkb.scanner.entity.DocumentCategory;
import com.bkb.scanner.entity.DocumentStatus;
import com.bkb.scanner.entity.RiskRating;
import com.bkb.scanner.service.DocumentRequirementsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentRequirementsServiceImpl implements DocumentRequirementsService {

    // Define document requirements for each customer type
    private final Map<CustomerType, List<DocumentDTO>> documentRequirements;

    // Define enhanced due diligence documents
    private final List<DocumentDTO> enhancedDueDiligenceDocuments;

    // Define high-risk jurisdictions
    private final List<String> highRiskJurisdictions;

    public DocumentRequirementsServiceImpl() {
        // Initialize high-risk jurisdictions
        this.highRiskJurisdictions = Arrays.asList(
                "North Korea", "Iran", "Syria", "Myanmar", "Yemen", "Afghanistan",
                "Cayman Islands", "Panama", "Bermuda", "Bahamas", "British Virgin Islands",
                "Isle of Man", "Jersey", "Guernsey", "Gibraltar", "Luxembourg", "Liechtenstein",
                "Vanuatu", "Samoa", "Labuan"
        );

        // Initialize enhanced due diligence documents
        this.enhancedDueDiligenceDocuments = Arrays.asList(
                createDocumentDTO("Detailed Source of Funds/Wealth Documentation", DocumentCategory.Financial),
                createDocumentDTO("PEP Declaration", DocumentCategory.Identification),
                createDocumentDTO("Tax Residency Self-Certification (CRS/FATCA)", DocumentCategory.Tax),
                createDocumentDTO("UBO Screening & Sanctions Check", DocumentCategory.Identification)
        );

        // Initialize document requirements for each customer type
        this.documentRequirements = new HashMap<>();

        // Individual requirements
        this.documentRequirements.put(CustomerType.Individual, Arrays.asList(
                createDocumentDTO("Passport Copy", DocumentCategory.Identification),
                createDocumentDTO("Proof of Address", DocumentCategory.Identification),
                createDocumentDTO("NRIC (for Singaporeans/PRs)", DocumentCategory.Identification),
                createDocumentDTO("Source of Funds Declaration", DocumentCategory.Financial)
        ));

        // Singapore Trust requirements
        this.documentRequirements.put(CustomerType.SingaporeTrust, Arrays.asList(
                createDocumentDTO("Trust Deed / Declaration of Trust", DocumentCategory.Legal),
                createDocumentDTO("ACRA Registration", DocumentCategory.Identification),
                createDocumentDTO("Trust Tax Identification Number (TIN)", DocumentCategory.Tax),
                createDocumentDTO("Bank Resolution", DocumentCategory.Legal),
                createDocumentDTO("List of Beneficiaries", DocumentCategory.Identification),
                createDocumentDTO("Statement of Trust Assets", DocumentCategory.Financial),
                createDocumentDTO("Professional Trustees' License", DocumentCategory.Identification),
                createDocumentDTO("Trustee NRIC/Passport", DocumentCategory.Identification),
                createDocumentDTO("Trustee Proof of Address", DocumentCategory.Identification)
        ));

        // Offshore Trust requirements
        this.documentRequirements.put(CustomerType.OffshoreTrust, Arrays.asList(
                createDocumentDTO("Notarized Trust Deed", DocumentCategory.Legal),
                createDocumentDTO("Certificate of Good Standing", DocumentCategory.Identification),
                createDocumentDTO("Legal Opinion", DocumentCategory.Legal),
                createDocumentDTO("Beneficial Ownership Declaration", DocumentCategory.Identification),
                createDocumentDTO("Source of Wealth & Source of Funds", DocumentCategory.Financial),
                createDocumentDTO("Trustee NRIC/Passport", DocumentCategory.Identification),
                createDocumentDTO("Trustee Proof of Address", DocumentCategory.Identification),
                createDocumentDTO("UBO Screening", DocumentCategory.Identification),
                createDocumentDTO("Tax Residency Documents", DocumentCategory.Tax)
        ));

        // Private Limited requirements
        this.documentRequirements.put(CustomerType.PrivateLimited, Arrays.asList(
                createDocumentDTO("Certificate of Incorporation (ACRA)", DocumentCategory.Identification),
                createDocumentDTO("Business Profile (ACRA BizFile)", DocumentCategory.Identification),
                createDocumentDTO("Company Constitution", DocumentCategory.Legal),
                createDocumentDTO("Board Resolution for Account Opening", DocumentCategory.Legal),
                createDocumentDTO("Company Tax Identification Number", DocumentCategory.Tax),
                createDocumentDTO("Ultimate Beneficial Owner Declaration", DocumentCategory.Identification),
                createDocumentDTO("Audited Financial Statements", DocumentCategory.Financial),
                createDocumentDTO("Proof of Business Activities", DocumentCategory.Identification),
                createDocumentDTO("Directors' NRIC/Passport", DocumentCategory.Identification),
                createDocumentDTO("Directors' Proof of Address", DocumentCategory.Identification)
        ));

        // Public Limited requirements
        this.documentRequirements.put(CustomerType.PublicLimited, Arrays.asList(
                createDocumentDTO("Certificate of Incorporation", DocumentCategory.Identification),
                createDocumentDTO("Company Constitution", DocumentCategory.Legal),
                createDocumentDTO("Annual Report / Audited Financial Statements", DocumentCategory.Financial),
                createDocumentDTO("Board Resolution for Account Opening", DocumentCategory.Legal),
                createDocumentDTO("SGX Listing Proof (if listed)", DocumentCategory.Identification),
                createDocumentDTO("Proof of Business Operations", DocumentCategory.Identification),
                createDocumentDTO("Directors' NRIC/Passport", DocumentCategory.Identification),
                createDocumentDTO("Directors' Proof of Address", DocumentCategory.Identification)
        ));

        // Continue defining requirements for other customer types...
        // Offshore Company requirements
        this.documentRequirements.put(CustomerType.OffshoreCompany, Arrays.asList(
                createDocumentDTO("Certificate of Incorporation (Notarized)", DocumentCategory.Identification),
                createDocumentDTO("Certificate of Good Standing", DocumentCategory.Identification),
                createDocumentDTO("Notarized M&AA / Constitution", DocumentCategory.Legal),
                createDocumentDTO("Register of Directors & Shareholders", DocumentCategory.Identification),
                createDocumentDTO("UBO Declaration", DocumentCategory.Identification),
                createDocumentDTO("Board Resolution for Account Opening", DocumentCategory.Legal),
                createDocumentDTO("Legal Opinion", DocumentCategory.Legal),
                createDocumentDTO("Proof of Business Activities", DocumentCategory.Identification),
                createDocumentDTO("Source of Funds", DocumentCategory.Financial),
                createDocumentDTO("Tax Residency Declaration", DocumentCategory.Tax)
        ));

        // Regulated Entity requirements
        this.documentRequirements.put(CustomerType.RegulatedEntity, Arrays.asList(
                createDocumentDTO("Regulatory License", DocumentCategory.Identification),
                createDocumentDTO("Company Constitution & Business Profile", DocumentCategory.Legal),
                createDocumentDTO("Board Resolution for Account Opening", DocumentCategory.Legal),
                createDocumentDTO("AML/Compliance Policy", DocumentCategory.Legal)
        ));

        // Crypto Business requirements
        this.documentRequirements.put(CustomerType.CryptoBusiness, Arrays.asList(
                createDocumentDTO("MAS License (Payment Services Act)", DocumentCategory.Identification),
                createDocumentDTO("Detailed AML Policy", DocumentCategory.Legal),
                createDocumentDTO("Proof of Regulatory Compliance", DocumentCategory.Identification),
                createDocumentDTO("Transaction Monitoring System Details", DocumentCategory.Identification),
                createDocumentDTO("Certificate of Incorporation", DocumentCategory.Identification),
                createDocumentDTO("Ultimate Beneficial Owner Declaration", DocumentCategory.Identification)
        ));

        // Sole Proprietor requirements
        this.documentRequirements.put(CustomerType.SoleProprietor, Arrays.asList(
                createDocumentDTO("Business Registration Certificate", DocumentCategory.Identification),
                createDocumentDTO("NRIC/Passport of Owner", DocumentCategory.Identification),
                createDocumentDTO("Proof of Address", DocumentCategory.Identification)
        ));

        // Partnership requirements
        this.documentRequirements.put(CustomerType.Partnership, Arrays.asList(
                createDocumentDTO("Business Registration Certificate", DocumentCategory.Identification),
                createDocumentDTO("Partnership Agreement", DocumentCategory.Legal),
                createDocumentDTO("Partners' NRIC/Passport", DocumentCategory.Identification),
                createDocumentDTO("Partners' Proof of Address", DocumentCategory.Identification)
        ));

        // Charity requirements
        this.documentRequirements.put(CustomerType.Charity, Arrays.asList(
                createDocumentDTO("Certificate of Charity Registration", DocumentCategory.Identification),
                createDocumentDTO("Source of Funds Declaration", DocumentCategory.Financial),
                createDocumentDTO("Board of Directors KYC", DocumentCategory.Identification),
                createDocumentDTO("Proof of Donations", DocumentCategory.Financial)
        ));

        // Government Entity requirements
        this.documentRequirements.put(CustomerType.GovernmentEntity, Arrays.asList(
                createDocumentDTO("Government Registration Documents", DocumentCategory.Identification),
                createDocumentDTO("Director KYC", DocumentCategory.Identification),
                createDocumentDTO("Board Resolution", DocumentCategory.Legal)
        ));

        // Family Office requirements
        this.documentRequirements.put(CustomerType.FamilyOffice, Arrays.asList(
                createDocumentDTO("MAS Notification (if licensed)", DocumentCategory.Identification),
                createDocumentDTO("UBO Documentation", DocumentCategory.Identification),
                createDocumentDTO("Source of Funds Declaration", DocumentCategory.Financial),
                createDocumentDTO("Certificate of Incorporation", DocumentCategory.Identification)
        ));
    }

    @Override
    public List<DocumentDTO> getRequiredDocuments(
            CustomerType customerType,
            Boolean isPEP,
            String registrationCountry,
            RiskRating riskRating) {

        // Start with base documents for the customer type
        List<DocumentDTO> requiredDocs = new ArrayList<>(documentRequirements.get(customerType));

        // Add EDD documents for high-risk customers
        if (Boolean.TRUE.equals(isPEP) ||
                isHighRiskJurisdiction(registrationCountry) ||
                riskRating == RiskRating.High ||
                riskRating == RiskRating.Extreme ||
                customerType == CustomerType.OffshoreCompany ||
                customerType == CustomerType.CryptoBusiness ||
                customerType == CustomerType.OffshoreTrust) {

            requiredDocs.addAll(enhancedDueDiligenceDocuments);
        }

        // Create fresh copies of the documents to avoid reference issues
        return requiredDocs.stream()
                .map(doc -> DocumentDTO.builder()
                        .filename(doc.getFilename())
                        .uploadedFile(null)
                        .uploadedName("")
                        .expiryDate(null)
                        .status(DocumentStatus.Pending)
                        .category(doc.getCategory())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isHighRiskJurisdiction(String country) {
        return highRiskJurisdictions.contains(country);
    }

    @Override
    public List<SelectOptionDTO> getCustomerTypeOptions() {
        return Arrays.stream(CustomerType.values())
                .map(type -> new SelectOptionDTO(type.name(), formatCustomerType(type)))
                .collect(Collectors.toList());
    }

    // Helper method to format customer type for display
    private String formatCustomerType(CustomerType type) {
        switch (type) {
            case PrivateLimited:
                return "Private Limited (Pte Ltd)";
            case PublicLimited:
                return "Public Limited (Ltd)";
            case OffshoreCompany:
                return "Offshore Company";
            case RegulatedEntity:
                return "Regulated Entity";
            case CryptoBusiness:
                return "Cryptocurrency / FinTech";
            case SoleProprietor:
                return "Sole Proprietor";
            case GovernmentEntity:
                return "Government Entity";
            case SingaporeTrust:
                return "Singapore Trust";
            case OffshoreTrust:
                return "Offshore Trust";
            case FamilyOffice:
                return "Family Office";
            default:
                return type.name();
        }
    }

    // Helper method to create document DTOs
    private DocumentDTO createDocumentDTO(String filename, DocumentCategory category) {
        return DocumentDTO.builder()
                .filename(filename)
                .uploadedFile(null)
                .uploadedName("")
                .expiryDate(null)
                .status(DocumentStatus.Pending)
                .category(category)
                .build();
    }
}