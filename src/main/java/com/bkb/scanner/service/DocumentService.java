// DocumentService.java
package com.bkb.scanner.service;

import com.bkb.scanner.dto.CustomerDTO;
import com.bkb.scanner.dto.DocumentDTO;
import com.bkb.scanner.dto.DocumentValidationResultDTO;
import com.bkb.scanner.dto.UploadResultDTO;
import com.bkb.scanner.entity.CustomerType;
import com.bkb.scanner.entity.DocumentCategory;
import com.bkb.scanner.entity.DocumentStatus;
import com.bkb.scanner.entity.RiskRating;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    boolean hasOutstandingDocuments(String basicNumber);

    List<CustomerDTO> getCustomersWithDocumentStatus(List<CustomerDTO> customers);

    List<DocumentDTO> getDocumentsForCustomer(String basicNumber);

    void updateDocumentStatus(String basicNumber, String filename, DocumentStatus status);

    UploadResultDTO uploadDocument(String basicNumber, String filename, MultipartFile file, String expiryDate);

    List<String> getOutstandingDocumentsList(String basicNumber);

    void initializeCustomerDocuments(
            String basicNumber,
            CustomerType customerType,
            Boolean isPEP,
            String registrationCountry,
            RiskRating riskRating);

    void updateCustomerDocuments(
            String basicNumber,
            CustomerType customerType,
            Boolean isPEP,
            String registrationCountry,
            RiskRating riskRating);

    List<DocumentCategory> getDocumentCategories();

    List<DocumentDTO> getDocumentsByCategory(String basicNumber, DocumentCategory category);

    int getDocumentUploadProgress(String basicNumber);

    DocumentValidationResultDTO validateDocuments(String basicNumber);

    boolean isDocumentRequiredForCustomerType(String documentName, CustomerType customerType);
}
