package com.bkb.scanner.config;

import com.bkb.scanner.entity.*;
import com.bkb.scanner.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final CaseRepository caseRepository;
    private final DocumentRepository documentRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final AccountStatusConfigRepository accountStatusConfigRepository;
    private final DocumentRequirementTemplateRepository documentRequirementTemplateRepository;
    private final ScanProfileRepository scanProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;
    private final JdbcTemplate jdbcTemplate;

    // Counter for unique activity IDs
    private final AtomicInteger activityCounter = new AtomicInteger(1);

    @Bean
    @Transactional
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("=== DataInitializer Started ===");
            log.info("Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
            log.info("Initializing database with seed data...");

            // Check if data already exists
            if (userRepository.count() > 0) {
                log.info("Database already contains {} users. Skipping initialization.", userRepository.count());
                return;
            }

            try {
                // Initialize configuration data
                initializeAccountTypes();
                initializeAccountStatusConfig();
                initializeDocumentRequirements();
                initializeScanProfiles();

                // Initialize users
                Map<String, User> users = initializeUsers();

                // Initialize parties
                Map<String, Party> parties = initializeParties();

                // Initialize documents
                Map<String, Document> documents = initializeDocuments(parties);

                // Initialize cases with all relationships
                initializeCases(users, parties, documents);

                log.info("Database initialization completed successfully!");
                log.info("Created {} users, {} parties, {} documents, and {} cases",
                        userRepository.count(),
                        partyRepository.count(),
                        documentRepository.count(),
                        caseRepository.count());
            } catch (Exception e) {
                log.error("Error during database initialization: ", e);
                throw e;
            }
        };
    }

    private Map<String, User> initializeUsers() {
        log.info("Initializing users...");

        Map<String, User> userMap = new HashMap<>();
        String encodedPassword = passwordEncoder.encode("password123");

        // Create RM user
        User rm = new User();
        rm.setUserId("U001");
        rm.setName("Jane Doe");
        rm.setEmail("jane.doe@example.com");
        rm.setPassword(encodedPassword);
        rm.setRole(User.UserRole.RM);
        rm.setDepartment("Corporate Banking");
        rm.setActive(true);
        rm.setEnabled(true);
        rm = userRepository.save(rm);
        userMap.put("RM", rm);
        log.info("Created user: {} with role {}", rm.getName(), rm.getRole());

        // Create Checker user
        User checker = new User();
        checker.setUserId("U002");
        checker.setName("John Smith");
        checker.setEmail("john.smith@example.com");
        checker.setPassword(encodedPassword);
        checker.setRole(User.UserRole.CHECKER);
        checker.setDepartment("Corporate Banking");
        checker.setActive(true);
        checker.setEnabled(true);
        checker = userRepository.save(checker);
        userMap.put("CHECKER", checker);
        log.info("Created user: {} with role {}", checker.getName(), checker.getRole());

        // Create Compliance user
        User compliance = new User();
        compliance.setUserId("U004");
        compliance.setName("Mary Anne");
        compliance.setEmail("mary.anne@example.com");
        compliance.setPassword(encodedPassword);
        compliance.setRole(User.UserRole.COMPLIANCE);
        compliance.setDepartment("Corporate Banking");
        compliance.setActive(true);
        compliance.setEnabled(true);
        compliance = userRepository.save(compliance);
        userMap.put("COMPLIANCE", compliance);
        log.info("Created user: {} with role {}", compliance.getName(), compliance.getRole());

        // Create GM user
        User gm = new User();
        gm.setUserId("U003");
        gm.setName("George Chan");
        gm.setEmail("george.chan@example.com");
        gm.setPassword(encodedPassword);
        gm.setRole(User.UserRole.GM);
        gm.setDepartment("Corporate Banking");
        gm.setActive(true);
        gm.setEnabled(true);
        gm = userRepository.save(gm);
        userMap.put("GM", gm);
        log.info("Created user: {} with role {}", gm.getName(), gm.getRole());

        // Create Admin user
        User admin = new User();
        admin.setUserId("U005");
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword(encodedPassword);
        admin.setRole(User.UserRole.ADMIN);
        admin.setDepartment("Corporate Banking");
        admin.setActive(true);
        admin.setEnabled(true);
        admin = userRepository.save(admin);
        userMap.put("ADMIN", admin);
        log.info("Created user: {} with role {}", admin.getName(), admin.getRole());

        log.info("Created {} users", userMap.size());
        return userMap;
    }

    private void initializeAccountTypes() {
        log.info("Initializing account types...");

        List<AccountType> accountTypes = Arrays.asList(
                createAccountType("CURRENT", "Current Account", "Standard business current account", 0,
                        new BigDecimal("10000"), new BigDecimal("5000")),
                createAccountType("SAVINGS", "Savings Account", "Business savings account", 1,
                        new BigDecimal("5000"), new BigDecimal("1000")),
                createAccountType("FOREIGN_CURRENCY", "Foreign Currency Account", "Multi-currency account", 2,
                        new BigDecimal("25000"), new BigDecimal("10000")),
                createAccountType("FIXED_DEPOSIT", "Fixed Deposit Account", "Term deposit account", 3,
                        new BigDecimal("50000"), new BigDecimal("50000"))
        );

        accountTypeRepository.saveAll(accountTypes);
        log.info("Created {} account types", accountTypes.size());
    }

    private AccountType createAccountType(String code, String displayName, String description,
                                          int sortOrder, BigDecimal minInitialDeposit, BigDecimal minBalance) {
        AccountType type = new AccountType();
        type.setCode(code);
        type.setDisplayName(displayName);
        type.setDescription(description);
        type.setSortOrder(sortOrder);
        type.setIsActive(true);
        type.setAllowOnlineBanking(true);
        type.setAllowCheckBook(true);
        type.setAllowDebitCard(true);
        type.setAllowForeignCurrency(code.equals("FOREIGN_CURRENCY"));
        type.setMinInitialDeposit(minInitialDeposit);
        type.setMinBalance(minBalance);
        type.setRequiredDocuments("Account Application Form,Signature Card");
        return type;
    }

    private void initializeAccountStatusConfig() {
        log.info("Initializing account status configurations...");

        List<AccountStatusConfig> statuses = Arrays.asList(
                createAccountStatus("PROPOSED", "Proposed", "Initial account proposal", 0,
                        "PENDING_CHECKER_REVIEW", "RM"),
                createAccountStatus("PENDING_CHECKER_REVIEW", "Pending Checker Review",
                        "Awaiting checker verification", 1, "PENDING_COMPLIANCE_REVIEW,REJECTED", "CHECKER"),
                createAccountStatus("PENDING_COMPLIANCE_REVIEW", "Pending Compliance Review",
                        "Awaiting compliance approval", 2, "ACTIVE,REJECTED", "COMPLIANCE"),
                createAccountStatus("ACTIVE", "Active", "Account is active and operational", 3,
                        "DORMANT,CLOSED", "COMPLIANCE"),
                createAccountStatus("REJECTED", "Rejected", "Account proposal rejected", 4,
                        "PROPOSED", "CHECKER,COMPLIANCE"),
                createAccountStatus("DORMANT", "Dormant", "Account is dormant due to inactivity", 5,
                        "ACTIVE,CLOSED", "COMPLIANCE"),
                createAccountStatus("CLOSED", "Closed", "Account has been closed", 6,
                        "", "COMPLIANCE")
        );

        accountStatusConfigRepository.saveAll(statuses);
        log.info("Created {} account status configurations", statuses.size());
    }

    private AccountStatusConfig createAccountStatus(String code, String displayName, String description,
                                                    int sortOrder, String nextStatuses, String requiredRole) {
        AccountStatusConfig status = new AccountStatusConfig();
        status.setCode(code);
        status.setDisplayName(displayName);
        status.setDescription(description);
        status.setSortOrder(sortOrder);
        status.setIsActive(true);
        status.setNextStatuses(nextStatuses);
        status.setRequiredRole(requiredRole);
        return status;
    }

    private void initializeDocumentRequirements() {
        log.info("Initializing document requirement templates...");

        List<DocumentRequirementTemplate> requirements = new ArrayList<>();

        // Entity-specific requirements
        requirements.add(createDocReq("ARCA / Questnet Search", true,
                "Company registry search", 1, DocumentRequirementTemplate.RequirementType.ENTITY_DOCUMENT,
                DocumentRequirementTemplate.RequirementCategory.CORPORATE, EntityData.EntityType.NON_LISTED_COMPANY));
        requirements.add(createDocReq("Certificate of Incorporation", true,
                "Company registration certificate", null, DocumentRequirementTemplate.RequirementType.ENTITY_DOCUMENT,
                DocumentRequirementTemplate.RequirementCategory.CORPORATE, EntityData.EntityType.NON_LISTED_COMPANY));
        requirements.add(createDocReq("Memorandum & Articles of Association", true,
                "Company constitution", null, DocumentRequirementTemplate.RequirementType.ENTITY_DOCUMENT,
                DocumentRequirementTemplate.RequirementCategory.CORPORATE, EntityData.EntityType.NON_LISTED_COMPANY));

        // Individual requirements - Singaporean/PR
        requirements.add(createIndividualDocReq("Identity Document / NRIC / Birth Certificate", true,
                "Primary identification", null, "Singaporean/PR"));

        // Individual requirements - Foreigner
        requirements.add(createIndividualDocReq("Passport", true,
                "Valid passport with at least 6 months validity", 6, "Foreigner"));
        requirements.add(createIndividualDocReq("Work Permit / Employment Pass", true,
                "Valid work authorization (if employed in Singapore)", null, "Foreigner"));
        requirements.add(createIndividualDocReq("Proof of Residential Address", true,
                "Utility bill or bank statement (not older than 3 months)", 3, "Foreigner"));

        // Bank forms
        requirements.add(createBankForm("Signature Card", true, "Account signatory authorization"));
        requirements.add(createBankForm("Board Resolutions", true, "Board approval for account opening"));
        requirements.add(createBankForm("Account Application Form", true, "Main account opening form"));
        requirements.add(createBankForm("Declaration of Beneficial Owner(s) Form", true, "UBO declaration"));
        requirements.add(createBankForm("KYC Form", true, "Know Your Customer questionnaire"));

        // Risk-based requirements
        requirements.add(createRiskBasedReq("Source of Wealth Declaration", true,
                "Required for high-risk cases", Case.RiskLevel.HIGH));
        requirements.add(createRiskBasedReq("GM Approval Memo", true,
                "General Manager approval for high-risk cases", Case.RiskLevel.HIGH));

        documentRequirementTemplateRepository.saveAll(requirements);
        log.info("Created {} document requirement templates", requirements.size());
    }

    private DocumentRequirementTemplate createDocReq(String name, boolean required, String description,
                                                     Integer validityMonths, DocumentRequirementTemplate.RequirementType type,
                                                     DocumentRequirementTemplate.RequirementCategory category,
                                                     EntityData.EntityType entityType) {
        DocumentRequirementTemplate req = new DocumentRequirementTemplate();
        req.setName(name);
        req.setRequired(required);
        req.setDescription(description);
        req.setValidityMonths(validityMonths);
        req.setRequirementType(type);
        req.setCategory(category);
        req.setEntityType(entityType);
        req.setIsActive(true);
        return req;
    }

    private DocumentRequirementTemplate createIndividualDocReq(String name, boolean required,
                                                               String description, Integer validityMonths,
                                                               String residencyStatus) {
        DocumentRequirementTemplate req = new DocumentRequirementTemplate();
        req.setName(name);
        req.setRequired(required);
        req.setDescription(description);
        req.setValidityMonths(validityMonths);
        req.setRequirementType(DocumentRequirementTemplate.RequirementType.INDIVIDUAL_DOCUMENT);
        req.setCategory(DocumentRequirementTemplate.RequirementCategory.IDENTITY);
        req.setResidencyStatus(residencyStatus);
        req.setIsActive(true);
        return req;
    }

    private DocumentRequirementTemplate createBankForm(String name, boolean required, String description) {
        DocumentRequirementTemplate req = new DocumentRequirementTemplate();
        req.setName(name);
        req.setRequired(required);
        req.setDescription(description);
        req.setRequirementType(DocumentRequirementTemplate.RequirementType.BANK_FORM);
        req.setCategory(DocumentRequirementTemplate.RequirementCategory.COMPLIANCE);
        req.setIsActive(true);
        return req;
    }

    private DocumentRequirementTemplate createRiskBasedReq(String name, boolean required,
                                                           String description, Case.RiskLevel riskLevel) {
        DocumentRequirementTemplate req = new DocumentRequirementTemplate();
        req.setName(name);
        req.setRequired(required);
        req.setDescription(description);
        req.setRequirementType(DocumentRequirementTemplate.RequirementType.RISK_BASED);
        req.setCategory(DocumentRequirementTemplate.RequirementCategory.COMPLIANCE);
        req.setRiskLevel(riskLevel);
        req.setIsActive(true);
        return req;
    }

    private void initializeScanProfiles() {
        log.info("Initializing scan profiles...");

        List<ScanProfile> profiles = Arrays.asList(
                createScanProfile("High Quality Color", "Color scan at 300 DPI", "Color", 300, "A4", false),
                createScanProfile("Standard Grayscale", "Grayscale scan at 200 DPI", "Grayscale", 200, "A4", false),
                createScanProfile("Black & White", "B&W scan at 150 DPI", "BlackWhite", 150, "A4", false),
                createScanProfile("Duplex Color", "Double-sided color scan", "Color", 300, "A4", true)
        );

        scanProfileRepository.saveAll(profiles);
        log.info("Created {} scan profiles", profiles.size());
    }

    private ScanProfile createScanProfile(String name, String description, String colorMode,
                                          Integer resolution, String paperSize, Boolean duplex) {
        ScanProfile profile = new ScanProfile();
        profile.setProfileName(name);
        profile.setDescription(description);
        profile.setColorMode(colorMode);
        profile.setResolution(resolution);
        profile.setPaperSize(paperSize);
        profile.setDuplex(duplex);
        profile.setIsActive(true);
        return profile;
    }

    private Map<String, Party> initializeParties() {
        log.info("Initializing parties...");

        Map<String, Party> partyMap = new HashMap<>();

        // Create individual parties
        Party johnTan = createIndividualParty("P001", "John Tan", "Singaporean/PR", false,
                "Singapore", "1980-05-15", "Male", "Director", "TechStart Innovations",
                "123 Main Street", "Singapore", "123456");

        Party michaelLim = createIndividualParty("P002", "Michael Lim", "Singaporean/PR", false,
                "Singapore", "1975-08-20", "Male", "CEO", "Various Companies",
                "456 Orchard Road", "Singapore", "238888");

        Party sarahChen = createIndividualParty("P003", "Sarah Chen", "Foreigner", false,
                "China", "1985-03-10", "Female", "Investment Manager", "Global Investments",
                "789 Marina Bay", "Singapore", "018956");

        Party davidLim = createIndividualParty("P004", "David Lim", "Singaporean/PR", false,
                "Singapore", "1970-12-25", "Male", "Partner", "Lim & Tan Legal Associates",
                "321 Bukit Timah Road", "Singapore", "259770");

        Party jessicaTan = createIndividualParty("P005", "Jessica Tan", "Singaporean/PR", false,
                "Singapore", "1982-06-18", "Female", "Partner", "Lim & Tan Legal Associates",
                "654 East Coast Road", "Singapore", "429123");

        Party robertWang = createIndividualParty("P006", "Robert Wang", "Foreigner", true,
                "United States", "1965-11-30", "Male", "Politician", "Government",
                "987 Sentosa Cove", "Singapore", "098297");

        // Add risk factors for PEP
        PartyRiskFactor pepRisk = new PartyRiskFactor();
        pepRisk.setPartyId("P006");
        pepRisk.setRiskFactor("Politically Exposed Person");
        pepRisk.setRiskCategory(PartyRiskFactor.RiskCategory.REGULATORY);
        pepRisk.setDescription("Senior government official in home country");
        pepRisk.setRiskScore(80);
        pepRisk.setIsActive(true);
        pepRisk.setIdentifiedDate(LocalDateTime.now().minusMonths(6));
        robertWang.addRiskFactor(pepRisk);

        List<Party> parties = Arrays.asList(johnTan, michaelLim, sarahChen, davidLim, jessicaTan, robertWang);
        partyRepository.saveAll(parties);

        parties.forEach(p -> partyMap.put(p.getPartyId(), p));

        log.info("Created {} parties", parties.size());
        return partyMap;
    }

    private Party createIndividualParty(String partyId, String name, String residencyStatus, boolean isPEP,
                                        String nationality, String dob, String gender, String occupation,
                                        String employer, String addressLine1, String city, String postalCode) {
        Party party = new Party();
        party.setPartyId(partyId);
        party.setName(name);
        party.setType(Party.PartyType.INDIVIDUAL);
        party.setResidencyStatus(residencyStatus);
        party.setNationality(nationality);
        party.setDateOfBirth(LocalDate.parse(dob));
        party.setGender(gender);
        party.setOccupation(occupation);
        party.setEmployer(employer);
        party.setIsPEP(isPEP);
        party.setIsSanctioned(false);
        party.setRiskScore(isPEP ? 80 : 20);

        // Set address
        Address address = new Address();
        address.setLine1(addressLine1);
        address.setCity(city);
        address.setPostalCode(postalCode);
        address.setCountry("Singapore");
        party.setAddress(address);

        // Set contact
        party.setEmail(name.toLowerCase().replace(" ", ".") + "@email.com");
        party.setPhone("+65 9" + String.format("%03d", new Random().nextInt(1000)) + " " +
                String.format("%04d", new Random().nextInt(10000)));

        party.setCreatedBy("System");

        return party;
    }

    private Map<String, Document> initializeDocuments(Map<String, Party> parties) {
        log.info("Initializing documents...");

        Map<String, Document> docMap = new HashMap<>();

        // Create verified documents for some parties
        Document doc1 = createDocument("DOC001", parties.get("P001").getPartyId(),
                "Identity Document / NRIC / Birth Certificate", Document.DocumentCategory.IDENTITY,
                "john_tan_nric.pdf", 2048000L, true, "Mary Anne");

        Document doc2 = createDocument("DOC002", parties.get("P004").getPartyId(),
                "Identity Document / NRIC / Birth Certificate", Document.DocumentCategory.IDENTITY,
                "david_lim_nric.pdf", 1536000L, true, "Mary Anne");

        Document doc3 = createDocument("DOC003", parties.get("P005").getPartyId(),
                "Identity Document / NRIC / Birth Certificate", Document.DocumentCategory.IDENTITY,
                "jessica_tan_nric.pdf", 1792000L, true, "Mary Anne");

        Document doc4 = createDocument("DOC004", parties.get("P006").getPartyId(),
                "Passport", Document.DocumentCategory.IDENTITY,
                "robert_wang_passport.pdf", 3072000L, true, "Mary Anne");

        // Add some unverified documents
        Document doc5 = createDocument("DOC005", parties.get("P002").getPartyId(),
                "Identity Document / NRIC / Birth Certificate", Document.DocumentCategory.IDENTITY,
                "michael_lim_nric.pdf", 1892000L, false, null);

        Document doc6 = createDocument("DOC006", parties.get("P003").getPartyId(),
                "Passport", Document.DocumentCategory.IDENTITY,
                "sarah_chen_passport.pdf", 2456000L, false, null);

        List<Document> documents = Arrays.asList(doc1, doc2, doc3, doc4, doc5, doc6);
        documentRepository.saveAll(documents);

        documents.forEach(d -> docMap.put(d.getDocId(), d));

        log.info("Created {} documents", documents.size());
        return docMap;
    }

    private Document createDocument(String docId, String ownerPartyId, String docType,
                                    Document.DocumentCategory category, String fileName,
                                    Long fileSize, boolean isVerified, String verifiedBy) {
        Document doc = new Document();
        doc.setDocId(docId);
        doc.setOwnerPartyId(ownerPartyId);
        doc.setDocType(docType);
        doc.setCategory(category);
        doc.setFileName(fileName);
        doc.setFileSize(fileSize);
        doc.setMimeType("application/pdf");
        doc.setIsVerified(isVerified);

        if (isVerified) {
            doc.setVerifiedBy(verifiedBy);
            doc.setVerifiedDate(LocalDateTime.now().minusDays(2));
            doc.setVerificationNotes("Document verified against original");
        }

        doc.setUploadedBy("Jane Doe");

        // For demo purposes, we'll just store a placeholder instead of actual file data
        doc.setFileData("DEMO_FILE_DATA_PLACEHOLDER".getBytes());

        return doc;
    }

    private void initializeCases(Map<String, User> users, Map<String, Party> parties, Map<String, Document> documents) {
        log.info("Initializing cases...");

        // Case 1: TechStart Innovations - Draft status
        Case case1 = createCase("CASE-2025-001", Case.CaseStatus.DRAFT, Case.RiskLevel.MEDIUM,
                users.get("RM").getName(), "TechStart Innovations Pte Ltd", EntityData.EntityType.NON_LISTED_COMPANY,
                "202412345A", "71 Ayer Rajah Crescent", "#02-18", "Singapore", "139951");

        // Add party link
        CasePartyLink link1 = createPartyLink(case1, parties.get("P001").getPartyId(), "Director", null, true);
        case1.getRelatedPartyLinks().add(link1);

        // Add document submission for already verified document
        CaseDocumentLink docLink1 = createDocumentLink(case1, "LNK-INIT-1", "req-party-P001-0", true);
        Submission sub1 = createSubmission(docLink1, "SUB-INIT-1", documents.get("DOC001").getDocId(),
                Submission.DocumentStatus.VERIFIED, users.get("RM").getName(),
                users.get("COMPLIANCE").getName(), LocalDateTime.now().minusDays(1));
        docLink1.getSubmissions().add(sub1);
        case1.getDocumentLinks().add(docLink1);

        // Add activity
        case1.getActivities().add(createActivity("A1", case1, "System", "Case Created"));

        caseRepository.save(case1);

        // Case 2: Lim & Tan Legal Associates - Pending Checker Review
        Case case2 = createCase("CASE-2025-002", Case.CaseStatus.PENDING_CHECKER_REVIEW, Case.RiskLevel.LOW,
                users.get("CHECKER").getName(), "Lim & Tan Legal Associates", EntityData.EntityType.PARTNERSHIP,
                "T12PF3456G", "1 Raffles Place", "#44-01", "Singapore", "048616");

        // Add party links
        case2.getRelatedPartyLinks().add(createPartyLink(case2, parties.get("P004").getPartyId(), "Partner", new BigDecimal("50"), true));
        case2.getRelatedPartyLinks().add(createPartyLink(case2, parties.get("P005").getPartyId(), "Partner", new BigDecimal("50"), true));

        // Add some document submissions
        CaseDocumentLink docLink2 = createDocumentLink(case2, "LNK-002-1", "req-entity-0", true);
        Submission sub2 = createSubmission(docLink2, "SUB-002-1", "DOC-TEMP-001",
                Submission.DocumentStatus.PENDING_CHECKER_VERIFICATION, users.get("RM").getName(), null, null);
        docLink2.getSubmissions().add(sub2);
        case2.getDocumentLinks().add(docLink2);

        case2.getActivities().add(createActivity("A1", case2, "System", "Case Created"));
        case2.getActivities().add(createActivity("A2", case2, users.get("RM").getName(), "Submitted for Review"));

        caseRepository.save(case2);

        // Case 3: Global Wealth Trust - Pending Compliance Review (High Risk)
        Case case3 = createCase("CASE-2025-003", Case.CaseStatus.PENDING_COMPLIANCE_REVIEW, Case.RiskLevel.HIGH,
                users.get("COMPLIANCE").getName(), "Global Wealth Trust", EntityData.EntityType.TRUST,
                "TRST98765B", "8 Marina Blvd", null, "Singapore", "018981");

        // Add PEP party
        case3.getRelatedPartyLinks().add(createPartyLink(case3, parties.get("P006").getPartyId(), "Settlor", null, true));

        // Add compliance notes
        case3.setComplianceNotes("High-risk case due to PEP involvement. Enhanced due diligence required.");

        case3.getActivities().add(createActivity("A1", case3, "System", "Case Created"));
        case3.getActivities().add(createActivity("A2", case3, users.get("RM").getName(), "Submitted for Review"));
        case3.getActivities().add(createActivity("A3", case3, users.get("CHECKER").getName(), "Approved by Checker"));

        caseRepository.save(case3);

        // Case 4: Active case with approved KYC and account
        Case case4 = createCase("CASE-2025-004", Case.CaseStatus.ACTIVE, Case.RiskLevel.LOW,
                users.get("RM").getName(), "ABC Trading Pte Ltd", EntityData.EntityType.NON_LISTED_COMPANY,
                "201812345K", "50 Raffles Place", "#30-01", "Singapore", "048623");

        // Create approved KYC snapshot
        ApprovalSnapshot kycSnapshot = new ApprovalSnapshot();
        kycSnapshot.setSnapshotId("SNAP-KYC-001");
        kycSnapshot.setCaseEntity(case4);
        kycSnapshot.setSnapshotType("KYC");
        kycSnapshot.setTimestamp(LocalDateTime.now().minusDays(5));
        kycSnapshot.setApprovedBy(users.get("COMPLIANCE").getName());
        kycSnapshot.setApproverRole("COMPLIANCE");
        kycSnapshot.setDecision("APPROVED");
        kycSnapshot.setRiskLevel("LOW");
        kycSnapshot.setChecklistCompleted(true);
        kycSnapshot.setValidUntil(LocalDate.now().plusYears(1));
        kycSnapshot.setPeriodicReviewRequired(true);
        kycSnapshot.setNextReviewDate(LocalDate.now().plusMonths(6));
        case4.setKycApprovalSnapshot(kycSnapshot);

        // Add an active account
        Account account = new Account();
        account.setAccountId("ACC-001");
        account.setCaseEntity(case4);
        account.setAccountNumber("001234567890");
        account.setAccountType("CURRENT");
        account.setStatus("ACTIVE");
        account.setCurrency("SGD");
        account.setPurpose("General business operations");
        account.setPrimaryHolderId("P002");
        account.setSignatureRules("Any one to sign");
        account.setOnlineBanking(true);
        account.setCheckBook(true);
        account.setDebitCard(true);
        account.setActivatedDate(LocalDateTime.now().minusDays(3));
        account.setActivatedBy(users.get("COMPLIANCE").getName());
        case4.getAccounts().add(account);

        case4.getActivities().add(createActivity("A1", case4, "System", "Case Created"));
        case4.getActivities().add(createActivity("A2", case4, users.get("GM").getName(), "KYC Approved"));
        case4.getActivities().add(createActivity("A3", case4, users.get("COMPLIANCE").getName(), "Account Activated"));

        caseRepository.save(case4);

        log.info("Created {} cases", 4);
    }

    private Case createCase(String caseId, Case.CaseStatus status, Case.RiskLevel riskLevel,
                            String assignedTo, String entityName, EntityData.EntityType entityType,
                            String taxId, String addressLine1, String addressLine2,
                            String city, String postalCode) {
        Case newCase = new Case();
        newCase.setCaseId(caseId);
        newCase.setStatus(status);
        newCase.setRiskLevel(riskLevel);
        newCase.setPriority(riskLevel == Case.RiskLevel.HIGH ? Case.Priority.HIGH : Case.Priority.NORMAL);
        newCase.setAssignedTo(assignedTo);

        // Create entity data
        EntityData entityData = new EntityData();
        entityData.setEntityName(entityName);
        entityData.setEntityType(entityType);
        entityData.setTaxId(taxId);

        Address registeredAddress = new Address();
        registeredAddress.setLine1(addressLine1);
        registeredAddress.setLine2(addressLine2);
        registeredAddress.setCity(city);
        registeredAddress.setPostalCode(postalCode);
        registeredAddress.setCountry("Singapore");
        entityData.setRegisteredAddress(registeredAddress);

        if (entityType != EntityData.EntityType.INDIVIDUAL_ACCOUNT) {
            entityData.setIncorporationDate(LocalDate.now().minusYears(5));
            entityData.setIncorporationCountry("Singapore");
            entityData.setIndustry(EntityData.Industry.PROFESSIONAL_SERVICES);
            entityData.setNumberOfEmployees("10-50");
            entityData.setAnnualRevenue("SGD 1-5 Million");
        }

        newCase.setEntityData(entityData);

        return newCase;
    }

    private CasePartyLink createPartyLink(Case parentCase, String partyId, String relationshipType,
                                          BigDecimal ownershipPercentage, boolean isPrimary) {
        CasePartyLink link = new CasePartyLink();
        link.setCaseEntity(parentCase);
        link.setPartyId(partyId);
        link.setRelationshipType(relationshipType);
        link.setOwnershipPercentage(ownershipPercentage);
        link.setStartDate(LocalDate.now());
        link.setIsPrimary(isPrimary);
        return link;
    }

    private CaseDocumentLink createDocumentLink(Case parentCase, String linkId, String requirementId, boolean isMandatory) {
        CaseDocumentLink link = new CaseDocumentLink();
        link.setLinkId(linkId);
        link.setCaseEntity(parentCase);
        link.setRequirementId(requirementId);
        link.setRequirementType("Standard");
        link.setIsMandatory(isMandatory);
        return link;
    }

    private Submission createSubmission(CaseDocumentLink parentLink, String submissionId, String masterDocId,
                                        Submission.DocumentStatus status, String submittedBy,
                                        String complianceReviewedBy, LocalDateTime complianceReviewedAt) {
        Submission submission = new Submission();
        submission.setSubmissionId(submissionId);
        submission.setDocumentLink(parentLink);
        submission.setMasterDocId(masterDocId);
        submission.setStatus(status);
        submission.setSubmittedAt(LocalDateTime.now().minusDays(3));
        submission.setSubmittedBy(submittedBy);
        submission.setSubmissionMethod(Submission.SubmissionMethod.UPLOAD);

        if (status == Submission.DocumentStatus.VERIFIED && complianceReviewedBy != null) {
            submission.setComplianceReviewedBy(complianceReviewedBy);
            submission.setComplianceReviewedAt(complianceReviewedAt);
        }

        return submission;
    }

    private ActivityLog createActivity(String id, Case parentCase, String actor, String action) {
        ActivityLog activity = new ActivityLog();
        // Use a unique activity ID using atomic counter
        activity.setActivityId("ACT-" + activityCounter.getAndIncrement());
        activity.setCaseEntity(parentCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(actor);
        activity.setActorRole(actor.equals("System") ? "SYSTEM" : "USER");
        activity.setActorId(actor.equals("System") ? "SYSTEM" : "U001");
        activity.setAction(action);
        activity.setActionType("CREATE");
        activity.setEntityType("Case");
        activity.setEntityId(parentCase.getCaseId());
        return activity;
    }
}