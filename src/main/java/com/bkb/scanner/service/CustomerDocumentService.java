package com.bkb.scanner.service;

import com.bkb.scanner.dto.CustomerDocumentDTO;
import com.bkb.scanner.dto.UploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CustomerDocumentService {

    List<CustomerDocumentDTO> getCustomerDocuments(String basicNumber);

    Optional<CustomerDocumentDTO> getCustomerDocument(String basicNumber, String documentName);

    Optional<CustomerDocumentDTO> getCustomerSignature(String basicNumber);

    UploadResultDTO updateCustomerDocument(String basicNumber, String documentName, MultipartFile file);

    boolean deleteCustomerDocument(String basicNumber, String documentName);

    List<CustomerDocumentDTO> getSignaturesForAccount(String accountNumber);

    boolean hasAllRequiredDocuments(String basicNumber, List<String> requiredDocuments);

    List<String> getMissingDocuments(String basicNumber, List<String> requiredDocuments);

    /**
     * Update expiry date for a customer document
     * @param basicNumber Customer's basic number
     * @param documentName Document name
     * @param expiryDate New expiry date in ISO format (YYYY-MM-DD)
     * @return true if update was successful
     */
    boolean updateDocumentExpiryDate(String basicNumber, String documentName, String expiryDate);


}