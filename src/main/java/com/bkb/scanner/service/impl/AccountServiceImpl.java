package com.bkb.scanner.service.impl;

import com.bkb.scanner.dto.*;
import com.bkb.scanner.entity.*;
import com.bkb.scanner.mapper.AccountDocumentMapper;
import com.bkb.scanner.mapper.AccountMapper;
import com.bkb.scanner.repository.AccountDocumentRepository;
import com.bkb.scanner.repository.AccountRepository;
import com.bkb.scanner.repository.CustomerRepository;
import com.bkb.scanner.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountDocumentRepository accountDocumentRepository;
    private final CustomerRepository customerRepository;
    private final AccountMapper accountMapper;
    private final AccountDocumentMapper accountDocumentMapper;
    private final Map<AccountType, AccountTypeDetailsDTO> accountTypeDetails;

    @Autowired
    public AccountServiceImpl(
            AccountRepository accountRepository,
            AccountDocumentRepository accountDocumentRepository,
            CustomerRepository customerRepository,
            AccountMapper accountMapper,
            AccountDocumentMapper accountDocumentMapper) {
        this.accountRepository = accountRepository;
        this.accountDocumentRepository = accountDocumentRepository;
        this.customerRepository = customerRepository;
        this.accountMapper = accountMapper;
        this.accountDocumentMapper = accountDocumentMapper;

        // Initialize account type details
        this.accountTypeDetails = initializeAccountTypeDetails();
    }

    private Map<AccountType, AccountTypeDetailsDTO> initializeAccountTypeDetails() {
        Map<AccountType, AccountTypeDetailsDTO> details = new HashMap<>();

        // Savings Account
        details.put(AccountType.SavingsAccount, AccountTypeDetailsDTO.builder()
                .name("Savings Account")
                .description("Basic account for saving money with interest earnings")
                .minimumBalance(new BigDecimal("500"))
                .maintenanceFee(new BigDecimal("2"))
                .interestRate(0.05)
                .isOfferedToIndividuals(true)
                .isOfferedToCorporates(false)
                .currenciesSupported(Arrays.asList(CurrencyCode.SGD, CurrencyCode.USD))
                .documentRequirements(getIndividualRequirements())
                .features(Arrays.asList(
                        "Monthly interest payments",
                        "ATM access",
                        "Mobile banking",
                        "Standing instructions"
                ))
                .build());

        // Current Account
        details.put(AccountType.CurrentAccount, AccountTypeDetailsDTO.builder()
                .name("Current Account")
                .description("Everyday banking account for transactions with checkbook")
                .minimumBalance(new BigDecimal("1000"))
                .maintenanceFee(new BigDecimal("5"))
                .interestRate(0.01)
                .isOfferedToIndividuals(true)
                .isOfferedToCorporates(true)
                .currenciesSupported(Arrays.asList(CurrencyCode.SGD, CurrencyCode.USD, CurrencyCode.EUR))
                .documentRequirements(getCombinedRequirements(
                        getStandardRequirements(),
                        Arrays.asList(
                                new AccountRequirementDTO("Proof of Address", true, "Identification"),
                                new AccountRequirementDTO("Checkbook Request Form", false, "Account")
                        )
                ))
                .features(Arrays.asList(
                        "Unlimited transactions",
                        "Checkbook facility",
                        "Overdraft protection (subject to approval)",
                        "Bill payment services"
                ))
                .build());

        // Add more account types...
        // Fixed Deposit
        details.put(AccountType.FixedDeposit, AccountTypeDetailsDTO.builder()
                .name("Fixed Deposit")
                .description("Term deposit with higher interest rates for fixed periods")
                .minimumBalance(new BigDecimal("5000"))
                .maintenanceFee(BigDecimal.ZERO)
                .interestRate(0.5)
                .isOfferedToIndividuals(true)
                .isOfferedToCorporates(true)
                .currenciesSupported(Arrays.asList(CurrencyCode.SGD, CurrencyCode.USD, CurrencyCode.EUR, CurrencyCode.GBP, CurrencyCode.AUD))
                .documentRequirements(getCombinedRequirements(
                        getStandardRequirements(),
                        Arrays.asList(
                                new AccountRequirementDTO("Term Instruction Form", true, "Account")
                        )
                ))
                .features(Arrays.asList(
                        "Higher interest rates",
                        "Flexible tenures (1/3/6/12/24 months)",
                        "Auto-renewal option",
                        "Premature withdrawal (subject to penalties)"
                ))
                .build());

        // Continue with other account types...

        return details;
    }

    // Helper methods to define document requirements
    private List<AccountRequirementDTO> getStandardRequirements() {
        return Arrays.asList(
                new AccountRequirementDTO("Account Opening Form", true, "Account"),
                new AccountRequirementDTO("Signature Specimen", true, "Account")
        );
    }

    private List<AccountRequirementDTO> getIndividualRequirements() {
        return getCombinedRequirements(
                getStandardRequirements(),
                Arrays.asList(
                        new AccountRequirementDTO("Proof of Income", true, "Financial"),
                        new AccountRequirementDTO("Tax Self-Declaration", true, "Tax")
                )
        );
    }

    private List<AccountRequirementDTO> getCorporateRequirements() {
        return getCombinedRequirements(
                getStandardRequirements(),
                Arrays.asList(
                        new AccountRequirementDTO("Board Resolution", true, "Legal"),
                        new AccountRequirementDTO("Company Mandate", true, "Legal"),
                        new AccountRequirementDTO("Authorized Signatories List", true, "Legal"),
                        new AccountRequirementDTO("Beneficial Ownership Declaration", true, "Identification")
                )
        );
    }

    private List<AccountRequirementDTO> getCombinedRequirements(List<AccountRequirementDTO> base, List<AccountRequirementDTO> additional) {
        List<AccountRequirementDTO> combined = new ArrayList<>(base);
        combined.addAll(additional);
        return combined;
    }

    @Override
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AccountDTO> getAccountByNumber(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .map(accountMapper::toDTO);
    }

    @Override
    public List<AccountDTO> getAccountsByCustomerNumber(String basicNumber) {
        List<Account> accounts = accountRepository.findAccountsByCustomerBasicNumber(basicNumber);
        return accounts.stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountTypeDetailsDTO getAccountTypeDetails(AccountType accountType) {
        return accountTypeDetails.get(accountType);
    }

    @Override
    public List<SelectOptionDTO> getAccountTypeOptions() {
        return accountTypeDetails.entrySet().stream()
                .map(entry -> new SelectOptionDTO(entry.getKey().name(), entry.getValue().getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountDTO createAccount(AccountCreateDTO accountDTO) {
        Customer customer = customerRepository.findByBasicNumber(accountDTO.getCustomerBasicNumber())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Generate a new account number
        String prefix = accountDTO.getAccountType().name().substring(0, 2).toUpperCase();

        // Find the last account number with this prefix
        List<Account> accounts = accountRepository.findAll();
        int lastAccountNum = accounts.stream()
                .filter(a -> a.getAccountNumber().startsWith(prefix))
                .map(a -> {
                    String[] parts = a.getAccountNumber().split("-");
                    return Integer.parseInt(parts[2]);
                })
                .max(Integer::compareTo)
                .orElse(1000);

        // Format: PREFIX2150-CUSTNUM-SEQUENTIAL
        String customerNum = accountDTO.getCustomerBasicNumber().replace("BN", "");
        String paddedCustomerNum = String.format("%03d", Integer.parseInt(customerNum));
        String newAccountNumber = String.format("%s2150-%s-%d", prefix, paddedCustomerNum, lastAccountNum + 1);

        // Get account type details
        AccountTypeDetailsDTO typeDetails = accountTypeDetails.get(accountDTO.getAccountType());

        // Create new account
        Account account = new Account();
        account.setAccountNumber(newAccountNumber);
        account.setAccountName(accountDTO.getAccountName());
        account.setAccountType(accountDTO.getAccountType());
        account.setStatus(AccountStatus.PendingApproval);
        account.setCurrency(accountDTO.getCurrency());
        account.setBalance(BigDecimal.ZERO);
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setOpeningDate(LocalDate.now());
        account.setLastTransactionDate(LocalDate.now());
        account.setCustomer(customer);
        account.setIsJoint(accountDTO.getIsJoint());

        if (accountDTO.getJointHolders() != null && !accountDTO.getJointHolders().isEmpty()) {
            account.setJointHolders(String.join(",", accountDTO.getJointHolders()));
        }

        account.setInterestRate(typeDetails != null ? typeDetails.getInterestRate() : 0.0);

        // Set account features
        account.setInternetBankingEnabled(false);
        account.setMobileAppEnabled(false);
        account.setDebitCardEnabled(false);
        account.setCheckbookEnabled(false);
        account.setStandingInstructionsEnabled(false);
        account.setDirectDebitEnabled(false);

        Account savedAccount = accountRepository.save(account);

        // Create required documents
        List<AccountRequirementDTO> docRequirements = typeDetails != null
                ? typeDetails.getDocumentRequirements()
                : new ArrayList<>();

        List<AccountDocument> documents = docRequirements.stream()
                .map(req -> {
                    AccountDocument doc = new AccountDocument();
                    doc.setDocumentName(req.getDocumentName());
                    doc.setUploadStatus(DocumentStatus.Pending);
                    doc.setAccount(savedAccount);
                    doc.setCategory(DocumentCategory.valueOf(req.getCategory()));
                    return doc;
                })
                .collect(Collectors.toList());

        accountDocumentRepository.saveAll(documents);

        return accountMapper.toDTO(savedAccount);
    }

    @Override
    @Transactional
    public AccountDTO updateAccount(String accountNumber, AccountUpdateDTO updates) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update account properties
        if (updates.getAccountName() != null) {
            account.setAccountName(updates.getAccountName());
        }

        if (updates.getStatus() != null) {
            account.setStatus(updates.getStatus());
        }

        if (updates.getInterestRate() != null) {
            account.setInterestRate(updates.getInterestRate());
        }

        if (updates.getMaturityDate() != null) {
            account.setMaturityDate(LocalDate.parse(updates.getMaturityDate()));
        }

        // Update account features
        if (updates.getInternetBankingEnabled() != null) {
            account.setInternetBankingEnabled(updates.getInternetBankingEnabled());
        }

        if (updates.getMobileAppEnabled() != null) {
            account.setMobileAppEnabled(updates.getMobileAppEnabled());
        }

        if (updates.getDebitCardEnabled() != null) {
            account.setDebitCardEnabled(updates.getDebitCardEnabled());
        }

        if (updates.getCheckbookEnabled() != null) {
            account.setCheckbookEnabled(updates.getCheckbookEnabled());
        }

        if (updates.getStandingInstructionsEnabled() != null) {
            account.setStandingInstructionsEnabled(updates.getStandingInstructionsEnabled());
        }

        if (updates.getDirectDebitEnabled() != null) {
            account.setDirectDebitEnabled(updates.getDirectDebitEnabled());
        }

        // Update joint account properties
        if (updates.getIsJoint() != null) {
            account.setIsJoint(updates.getIsJoint());
        }

        if (updates.getJointHolders() != null) {
            if (updates.getJointHolders().isEmpty()) {
                account.setJointHolders(null);
            } else {
                account.setJointHolders(String.join(",", updates.getJointHolders()));
            }
        }

        // Update account manager if provided
        if (updates.getAccountManager() != null) {
            account.setAccountManager(updates.getAccountManager());
        }

        // Update branch code if provided
        if (updates.getBranchCode() != null) {
            account.setBranchCode(updates.getBranchCode());
        }

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDTO(savedAccount);
    }

    @Override
    @Transactional
    public AccountDTO changeAccountStatus(String accountNumber, AccountStatus newStatus) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus(newStatus);
        Account savedAccount = accountRepository.save(account);

        return accountMapper.toDTO(savedAccount);
    }

    @Override
    public List<AccountDTO> getAccountsByStatus(AccountStatus status) {
        List<Account> accounts = accountRepository.findByStatus(status);
        return accounts.stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UploadResultDTO updateAccountDocument(String accountNumber, String documentName, MultipartFile file) {
        try {
            Account account = accountRepository.findById(accountNumber)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            AccountDocument document = accountDocumentRepository
                    .findByAccountAccountNumberAndDocumentName(accountNumber, documentName)
                    .orElseGet(() -> {
                        // Create new document if it doesn't exist
                        AccountDocument newDoc = new AccountDocument();
                        newDoc.setDocumentName(documentName);
                        newDoc.setAccount(account);
                        newDoc.setCategory(DocumentCategory.Identification); // Default category
                        return newDoc;
                    });

            document.setUploadStatus(DocumentStatus.Uploaded);
            document.setUploadDate(LocalDate.now());
            document.setFileContent(file.getBytes());
            document.setContentType(file.getContentType());

            accountDocumentRepository.save(document);

            return new UploadResultDTO(true, "Document uploaded successfully");
        } catch (IOException e) {
            return new UploadResultDTO(false, "Failed to upload document: " + e.getMessage());
        } catch (Exception e) {
            return new UploadResultDTO(false, "Error: " + e.getMessage());
        }
    }

    @Override
    public boolean hasAllRequiredDocuments(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        AccountTypeDetailsDTO typeDetails = accountTypeDetails.get(account.getAccountType());
        if (typeDetails == null) {
            return false;
        }

        List<String> requiredDocs = typeDetails.getDocumentRequirements().stream()
                .filter(AccountRequirementDTO::getIsRequired)
                .map(AccountRequirementDTO::getDocumentName)
                .collect(Collectors.toList());

        return accountDocumentRepository.hasAllRequiredDocuments(accountNumber, requiredDocs);
    }

    @Override
    public List<String> getMissingDocuments(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        AccountTypeDetailsDTO typeDetails = accountTypeDetails.get(account.getAccountType());
        if (typeDetails == null) {
            return Collections.emptyList();
        }

        List<String> requiredDocs = typeDetails.getDocumentRequirements().stream()
                .filter(AccountRequirementDTO::getIsRequired)
                .map(AccountRequirementDTO::getDocumentName)
                .collect(Collectors.toList());

        return accountDocumentRepository.getMissingDocuments(accountNumber, requiredDocs);
    }

    @Override
    public AccountSummaryDTO getAccountSummary() {
        long totalAccounts = accountRepository.count();
        long activeAccounts = accountRepository.findByStatus(AccountStatus.Active).size();
        long pendingAccounts = accountRepository.findByStatus(AccountStatus.PendingApproval).size();
        long frozenAccounts = accountRepository.findByStatus(AccountStatus.Frozen).size();
        long dormantAccounts = accountRepository.findByStatus(AccountStatus.Dormant).size();
        long closedAccounts = accountRepository.findByStatus(AccountStatus.Closed).size();

        // Get balances by currency
        List<Map<String, Object>> balancesByCurrency = accountRepository.getTotalBalancesByCurrency();
        Map<String, BigDecimal> balancesMap = balancesByCurrency.stream()
                .collect(Collectors.toMap(
                        entry -> ((CurrencyCode) entry.get("currency")).name(),
                        entry -> (BigDecimal) entry.get("totalBalance")
                ));

        // Get account type distribution
        List<Map<String, Object>> typeDistribution = accountRepository.getAccountTypeDistribution();
        Map<String, Long> distributionMap = typeDistribution.stream()
                .collect(Collectors.toMap(
                        entry -> ((AccountType) entry.get("type")).name(),
                        entry -> ((Number) entry.get("count")).longValue()
                ));

        return AccountSummaryDTO.builder()
                .totalAccounts(totalAccounts)
                .activeAccounts(activeAccounts)
                .pendingAccounts(pendingAccounts)
                .frozenAccounts(frozenAccounts)
                .dormantAccounts(dormantAccounts)
                .closedAccounts(closedAccounts)
                .balancesByCurrency(balancesMap)
                .accountTypeDistribution(distributionMap)
                .build();
    }
}