
// DocumentServiceImpl.java
package com.bkb.scanner.service.impl;

import com.bkb.scanner.dto.CustomerDTO;
import com.bkb.scanner.dto.DocumentDTO;
import com.bkb.scanner.dto.DocumentValidationResultDTO;
import com.bkb.scanner.dto.UploadResultDTO;
import com.bkb.scanner.entity.*;
import com.bkb.scanner.mapper.DocumentMapper;
import com.bkb.scanner.repository.CustomerRepository;
import com.bkb.scanner.repository.DocumentRepository;
import com.bkb.scanner.service.DocumentRequirementsService;
import com.bkb.scanner.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private final DocumentMapper documentMapper;
    private final DocumentRequirementsService documentRequirementsService;

    @Autowired
    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            CustomerRepository customerRepository,
            DocumentMapper documentMapper,
            DocumentRequirementsService documentRequirementsService) {
        this.documentRepository = documentRepository;
        this.customerRepository = customerRepository;
        this.documentMapper = documentMapper;
        this.documentRequirementsService = documentRequirementsService;
    }

    @Override
    public boolean hasOutstandingDocuments(String basicNumber) {
        return documentRepository.hasOutstandingDocuments(basicNumber);
    }

    @Override
    public List<CustomerDTO> getCustomersWithDocumentStatus(List<CustomerDTO> customers) {
        for (CustomerDTO customer : customers) {
            boolean hasOutstanding = hasOutstandingDocuments(customer.getBasicNumber());
            customer.setHasOutstandingDocuments(hasOutstanding);
        }
        return customers;
    }

    @Override
    public List<DocumentDTO> getDocumentsForCustomer(String basicNumber) {
        List<Document> documents = documentRepository.findByCustomerBasicNumber(basicNumber);
        return documents.stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDocumentStatus(String basicNumber, String filename, DocumentStatus status) {
        List<Document> documents = documentRepository.findByCustomerBasicNumber(basicNumber);
        documents.stream()
                .filter(doc -> doc.getFilename().equals(filename))
                .findFirst()
                .ifPresent(doc -> {
                    doc.setStatus(status);
                    if (status == DocumentStatus.Uploaded && doc.getUploadedDate() == null) {
                        doc.setUploadedDate(LocalDate.now());
                    }
                    documentRepository.save(doc);

                    // Update customer's hasOutstandingDocuments flag
                    updateCustomerDocumentStatus(basicNumber);
                });
    }

    @Override
    @Transactional
    public UploadResultDTO uploadDocument(String basicNumber, String filename, MultipartFile file, String expiryDate) {
        try {
            Customer customer = customerRepository.findByBasicNumber(basicNumber)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            List<Document> documents = documentRepository.findByCustomerBasicNumber(basicNumber);
            Document document = documents.stream()
                    .filter(doc -> doc.getFilename().equals(filename))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            document.setStatus(DocumentStatus.Uploaded);
            document.setUploadedName(file.getOriginalFilename());
            document.setContentType(file.getContentType());
            document.setFileContent(file.getBytes());
            document.setUploadedDate(LocalDate.now());

            if (expiryDate != null && !expiryDate.isEmpty()) {
                document.setExpiryDate(LocalDate.parse(expiryDate));
            }

            documentRepository.save(document);

            // Update customer's hasOutstandingDocuments flag
            updateCustomerDocumentStatus(basicNumber);

            return new UploadResultDTO(true, "Document uploaded successfully");
        } catch (IOException e) {
            return new UploadResultDTO(false, "Failed to upload document: " + e.getMessage());
        } catch (Exception e) {
            return new UploadResultDTO(false, "Error: " + e.getMessage());
        }
    }

    @Override
    public List<String> getOutstandingDocumentsList(String basicNumber) {
        return documentRepository.getOutstandingDocumentsList(basicNumber);
    }

    @Override
    @Transactional
    public void initializeCustomerDocuments(
            String basicNumber,
            CustomerType customerType,
            Boolean isPEP,
            String registrationCountry,
            RiskRating riskRating) {

        // Check if customer exists
        Customer customer = customerRepository.findByBasicNumber(basicNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Check if documents already exist for this customer
        List<Document> existingDocs = documentRepository.findByCustomerBasicNumber(basicNumber);
        if (!existingDocs.isEmpty()) {
            return; // Already initialized
        }

        // Get required document templates
        List<DocumentDTO> requiredTemplates = documentRequirementsService.getRequiredDocuments(
                customerType,
                isPEP,
                registrationCountry,
                riskRating
        );

        // Create document entities
        List<Document> documents = requiredTemplates.stream()
                .map(template -> {
                    Document doc = new Document();
                    doc.setFilename(template.getFilename());
                    doc.setStatus(DocumentStatus.Pending);
                    doc.setCategory(template.getCategory());
                    doc.setCustomer(customer);
                    return doc;
                })
                .collect(Collectors.toList());

        // Save all documents
        documentRepository.saveAll(documents);

        // Update customer's hasOutstandingDocuments flag
        customer.setHasOutstandingDocuments(true);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void updateCustomerDocuments(
            String basicNumber,
            CustomerType customerType,
            Boolean isPEP,
            String registrationCountry,
            RiskRating riskRating) {

        // Check if customer exists
        Customer customer = customerRepository.findByBasicNumber(basicNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Get current documents
        List<Document> currentDocs = documentRepository.findByCustomerBasicNumber(basicNumber);

        if (currentDocs.isEmpty()) {
            // If no documents exist, initialize them
            initializeCustomerDocuments(basicNumber, customerType, isPEP, registrationCountry, riskRating);
            return;
        }

        // Get required document templates based on updated profile
        List<DocumentDTO> requiredTemplates = documentRequirementsService.getRequiredDocuments(
                customerType,
                isPEP,
                registrationCountry,
                riskRating
        );

        // Create a map of existing documents by filename
        Map<String, Document> existingDocMap = currentDocs.stream()
                .collect(Collectors.toMap(Document::getFilename, doc -> doc));

        // Create new documents list
        List<Document> updatedDocs = new ArrayList<>();

        for (DocumentDTO template : requiredTemplates) {
            Document existingDoc = existingDocMap.get(template.getFilename());

            if (existingDoc != null) {
                // Keep existing document
                updatedDocs.add(existingDoc);
                existingDocMap.remove(template.getFilename());
            } else {
                // Create new document
                Document newDoc = new Document();
                newDoc.setFilename(template.getFilename());
                newDoc.setStatus(DocumentStatus.Pending);
                newDoc.setCategory(template.getCategory());
                newDoc.setCustomer(customer);
                updatedDocs.add(newDoc);
            }
        }

        // Delete documents that are no longer required
        List<Document> docsToRemove = new ArrayList<>(existingDocMap.values());
        documentRepository.deleteAll(docsToRemove);

        // Save all documents
        documentRepository.saveAll(updatedDocs);

        // Update customer's hasOutstandingDocuments flag
        updateCustomerDocumentStatus(basicNumber);
    }

    @Override
    public List<DocumentCategory> getDocumentCategories() {
        return Arrays.asList(DocumentCategory.values());
    }

    @Override
    public List<DocumentDTO> getDocumentsByCategory(String basicNumber, DocumentCategory category) {
        List<Document> documents = documentRepository.findByCustomerBasicNumberAndCategory(basicNumber, category);
        return documents.stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public int getDocumentUploadProgress(String basicNumber) {
        int totalDocs = documentRepository.countDocumentsByCustomer(basicNumber);
        if (totalDocs == 0) {
            return 0;
        }

        int uploadedDocs = documentRepository.countUploadedDocumentsByCustomer(basicNumber);
        return (int) Math.round((double) uploadedDocs / totalDocs * 100);
    }

    @Override
    public DocumentValidationResultDTO validateDocuments(String basicNumber) {
        List<String> missingDocuments = documentRepository.getOutstandingDocumentsList(basicNumber);

        boolean isComplete = missingDocuments.isEmpty();

        return new DocumentValidationResultDTO(isComplete, missingDocuments);
    }

    @Override
    public boolean isDocumentRequiredForCustomerType(String documentName, CustomerType customerType) {
        List<DocumentDTO> requiredDocs = documentRequirementsService.getRequiredDocuments(customerType, false, "Singapore", RiskRating.Low);
        return requiredDocs.stream()
                .anyMatch(doc -> doc.getFilename().equals(documentName));
    }

    // Helper method to update customer's hasOutstandingDocuments flag
    private void updateCustomerDocumentStatus(String basicNumber) {
        Customer customer = customerRepository.findByBasicNumber(basicNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        boolean hasOutstanding = documentRepository.hasOutstandingDocuments(basicNumber);
        customer.setHasOutstandingDocuments(hasOutstanding);

        customerRepository.save(customer);
    }
}