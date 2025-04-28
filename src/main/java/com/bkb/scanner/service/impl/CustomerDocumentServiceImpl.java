package com.bkb.scanner.service.impl;

import com.bkb.scanner.dto.CustomerDocumentDTO;
import com.bkb.scanner.dto.UploadResultDTO;
import com.bkb.scanner.entity.Account;
import com.bkb.scanner.entity.Customer;
import com.bkb.scanner.entity.CustomerDocument;
import com.bkb.scanner.entity.DocumentCategory;
import com.bkb.scanner.entity.DocumentStatus;
import com.bkb.scanner.mapper.CustomerDocumentMapper;
import com.bkb.scanner.repository.AccountRepository;
import com.bkb.scanner.repository.CustomerDocumentRepository;
import com.bkb.scanner.repository.CustomerRepository;
import com.bkb.scanner.service.CustomerDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerDocumentServiceImpl implements CustomerDocumentService {

    private static final Logger log = LoggerFactory.getLogger(CustomerDocumentServiceImpl.class);

    private final CustomerDocumentRepository customerDocumentRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CustomerDocumentMapper customerDocumentMapper;

    @Autowired
    public CustomerDocumentServiceImpl(
            CustomerDocumentRepository customerDocumentRepository,
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            CustomerDocumentMapper customerDocumentMapper) {
        this.customerDocumentRepository = customerDocumentRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.customerDocumentMapper = customerDocumentMapper;
    }

    @Override
    public List<CustomerDocumentDTO> getCustomerDocuments(String basicNumber) {
        List<CustomerDocument> documents = customerDocumentRepository.findByCustomerBasicNumber(basicNumber);
        return documents.stream()
                .map(customerDocumentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDocumentDTO> getCustomerDocument(String basicNumber, String documentName) {
        return customerDocumentRepository.findByCustomerBasicNumberAndDocumentName(basicNumber, documentName)
                .map(customerDocumentMapper::toDTO);
    }

    @Override
    public Optional<CustomerDocumentDTO> getCustomerSignature(String basicNumber) {
        return getCustomerDocument(basicNumber, "Signature Specimen");
    }

    /**
     * Upload or update a customer document
     * FIXED METHOD: No longer catches exceptions that would trigger transaction rollback
     */
    @Override
    @Transactional
    public UploadResultDTO updateCustomerDocument(String basicNumber, String documentName, MultipartFile file) {
        log.info("Starting document upload for customer: {}, document: {}", basicNumber, documentName);

        // Find the customer - will throw exception if not found
        Customer customer = customerRepository.findByBasicNumber(basicNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + basicNumber));

        log.info("Found customer: {}", customer.getName());

        // Find existing document or create new one
        CustomerDocument document = customerDocumentRepository
                .findByCustomerBasicNumberAndDocumentName(basicNumber, documentName)
                .orElseGet(() -> {
                    log.info("Creating new document record for: {}", documentName);
                    // Create new document if it doesn't exist
                    CustomerDocument newDoc = new CustomerDocument();
                    newDoc.setDocumentName(documentName);
                    newDoc.setCustomer(customer);

                    // Determine category based on document name
                    DocumentCategory category = determineCategory(documentName);
                    log.info("Determined category for '{}': {}", documentName, category);
                    newDoc.setCategory(category);

                    return newDoc;
                });

        // Update document properties
        document.setUploadStatus(DocumentStatus.Uploaded);
        document.setUploadDate(LocalDate.now());

        try {
            // Set file content
            document.setFileContent(file.getBytes());
            document.setContentType(file.getContentType());
            log.info("File content read successfully. Size: {} bytes, Type: {}",
                    file.getSize(), file.getContentType());
        } catch (IOException e) {
            log.error("Failed to read file contents", e);
            return new UploadResultDTO(false, "Failed to read file contents: " + e.getMessage());
        }

        // Save the document
        CustomerDocument savedDocument = customerDocumentRepository.save(document);
        log.info("Document saved successfully with ID: {}", savedDocument.getId());

        return new UploadResultDTO(true, "Document uploaded successfully");
    }

    @Override
    @Transactional
    public boolean deleteCustomerDocument(String basicNumber, String documentName) {
        Optional<CustomerDocument> document = customerDocumentRepository
                .findByCustomerBasicNumberAndDocumentName(basicNumber, documentName);

        if (document.isPresent()) {
            customerDocumentRepository.delete(document.get());
            log.info("Deleted document: {} for customer: {}", documentName, basicNumber);
            return true;
        }
        log.info("Document not found to delete: {} for customer: {}", documentName, basicNumber);
        return false;
    }

    @Override
    public List<CustomerDocumentDTO> getSignaturesForAccount(String accountNumber) {
        // Get the account
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found with number: " + accountNumber));

        List<String> customerIds = new ArrayList<>();

        // Add primary account holder
        if (account.getCustomer() != null) {
            customerIds.add(account.getCustomer().getBasicNumber());
        }

        // Add joint holders if any
        if (account.getIsJoint() && account.getJointHolders() != null && !account.getJointHolders().isEmpty()) {
            String[] jointHolderIds = account.getJointHolders().split(",");
            customerIds.addAll(Arrays.asList(jointHolderIds));
        }

        // Fetch signatures for all customers
        List<CustomerDocumentDTO> signatures = new ArrayList<>();
        for (String customerId : customerIds) {
            customerDocumentRepository
                    .findByCustomerBasicNumberAndDocumentName(customerId, "Signature Specimen")
                    .ifPresent(doc -> signatures.add(customerDocumentMapper.toDTO(doc)));
        }

        return signatures;
    }

    @Override
    public boolean hasAllRequiredDocuments(String basicNumber, List<String> requiredDocuments) {
        if (requiredDocuments == null || requiredDocuments.isEmpty()) {
            return true;
        }

        long count = requiredDocuments.size();
        return customerDocumentRepository.hasAllRequiredDocuments(basicNumber, requiredDocuments, count);
    }

    @Override
    public List<String> getMissingDocuments(String basicNumber, List<String> requiredDocuments) {
        if (requiredDocuments == null || requiredDocuments.isEmpty()) {
            return new ArrayList<>();
        }

        // Get the list of uploaded documents for this customer
        List<String> uploadedDocuments = customerDocumentRepository.findUploadedDocumentNames(basicNumber);

        // Calculate missing documents (required documents that are not in the uploaded list)
        return requiredDocuments.stream()
                .filter(reqDoc -> !uploadedDocuments.contains(reqDoc))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to determine document category based on document name
     */
    private DocumentCategory determineCategory(String documentName) {
        String docNameLower = documentName.toLowerCase();

        if (docNameLower.contains("signature") || docNameLower.contains("sign specimen")) {
            return DocumentCategory.Signature;  // Changed from Account to Signature
        } else if (docNameLower.contains("passport") || docNameLower.contains("id") ||
                docNameLower.contains("license") || docNameLower.contains("identity")) {
            return DocumentCategory.Identification;
        } else if (docNameLower.contains("income") || docNameLower.contains("statement") ||
                docNameLower.contains("financial")) {
            return DocumentCategory.Financial;
        } else if (docNameLower.contains("kyc") || docNameLower.contains("know your customer")) {
            return DocumentCategory.KYC;  // Added KYC category
        } else if (docNameLower.contains("tax")) {
            return DocumentCategory.Tax;
        } else if (docNameLower.contains("agreement") || docNameLower.contains("contract") ||
                docNameLower.contains("terms")) {
            return DocumentCategory.Legal;
        } else if (docNameLower.contains("authorization") || docNameLower.contains("power of attorney")) {
            return DocumentCategory.Authorization;  // Added Authorization category
        } else {
            return DocumentCategory.Other;
        }
    }

    @Override
    @Transactional
    public boolean updateDocumentExpiryDate(String basicNumber, String documentName, String expiryDate) {
        // Find the document in your database
        Optional<CustomerDocument> documentOpt = customerDocumentRepository.findByCustomerBasicNumberAndDocumentName(basicNumber, documentName);

        if (documentOpt.isPresent()) {
            CustomerDocument document = documentOpt.get();

            // Parse the expiry date string to LocalDate
            try {
                LocalDate parsedDate = LocalDate.parse(expiryDate);

                // Update the document
                document.setExpiryDate(parsedDate);
                customerDocumentRepository.save(document);

                log.info("Updated expiry date for document: {} to {}", documentName, expiryDate);
                return true;
            } catch (DateTimeParseException e) {
                log.error("Invalid date format: {}", expiryDate, e);
                return false;
            }
        }

        log.warn("Document not found to update expiry date: {} for customer: {}", documentName, basicNumber);
        return false;
    }
}