package com.bkb.scanner.controller;

import com.bkb.scanner.dto.CustomerDocumentDTO;
import com.bkb.scanner.dto.UploadResultDTO;
import com.bkb.scanner.service.CustomerDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer-documents")
@CrossOrigin
public class CustomerDocumentController {

    private final CustomerDocumentService customerDocumentService;

    @Autowired
    public CustomerDocumentController(CustomerDocumentService customerDocumentService) {
        this.customerDocumentService = customerDocumentService;
    }

    /**
     * Get all documents for a customer
     */
    @GetMapping("/{basicNumber}")
    public ResponseEntity<List<CustomerDocumentDTO>> getCustomerDocuments(@PathVariable String basicNumber) {
        List<CustomerDocumentDTO> documents = customerDocumentService.getCustomerDocuments(basicNumber);
        return ResponseEntity.ok(documents);
    }

    /**
     * Get a specific document for a customer
     */
    @GetMapping("/{basicNumber}/{documentName}")
    public ResponseEntity<?> getCustomerDocument(
            @PathVariable String basicNumber,
            @PathVariable String documentName,
            @RequestParam(required = false, defaultValue = "false") boolean download) {

        return customerDocumentService.getCustomerDocument(basicNumber, documentName)
                .map(doc -> {
                    if (download && doc.getFileContent() != null) {
                        // Configure response for file download
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.parseMediaType(doc.getContentType()));
                        String filename = doc.getDocumentName().replace(" ", "_") + getFileExtension(doc.getContentType());
                        headers.setContentDispositionFormData("attachment", filename);

                        return new ResponseEntity<>(doc.getFileContent(), headers, HttpStatus.OK);
                    } else {
                        // Return the document DTO
                        return ResponseEntity.ok(doc);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get customer signature
     */
    @GetMapping("/{basicNumber}/signature")
    public ResponseEntity<?> getCustomerSignature(
            @PathVariable String basicNumber,
            @RequestParam(required = false, defaultValue = "false") boolean download) {

        return customerDocumentService.getCustomerSignature(basicNumber)
                .map(doc -> {
                    if (download && doc.getFileContent() != null) {
                        // Configure response for file download
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.parseMediaType(doc.getContentType()));
                        String filename = "Signature_" + basicNumber + getFileExtension(doc.getContentType());
                        headers.setContentDispositionFormData("attachment", filename);

                        return new ResponseEntity<>(doc.getFileContent(), headers, HttpStatus.OK);
                    } else {
                        // Return the document DTO
                        return ResponseEntity.ok(doc);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all signatures for an account
     */
    @GetMapping("/account/{accountNumber}/signatures")
    public ResponseEntity<List<CustomerDocumentDTO>> getSignaturesForAccount(@PathVariable String accountNumber) {
        List<CustomerDocumentDTO> signatures = customerDocumentService.getSignaturesForAccount(accountNumber);
        return ResponseEntity.ok(signatures);
    }

    /**
     * Upload a document for a customer
     */
    @PostMapping("/{basicNumber}/{documentName}/upload")
    public ResponseEntity<UploadResultDTO> uploadDocument(
            @PathVariable String basicNumber,
            @PathVariable String documentName,
            @RequestParam("file") MultipartFile file) {

        UploadResultDTO result = customerDocumentService.updateCustomerDocument(basicNumber, documentName, file);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete a customer document
     */
    @DeleteMapping("/{basicNumber}/{documentName}")
    public ResponseEntity<?> deleteDocument(
            @PathVariable String basicNumber,
            @PathVariable String documentName) {

        boolean result = customerDocumentService.deleteCustomerDocument(basicNumber, documentName);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update expiry date for a customer document
     */
    @PutMapping("/{basicNumber}/{documentName}/expiry")
    public ResponseEntity<?> updateDocumentExpiryDate(
            @PathVariable String basicNumber,
            @PathVariable String documentName,
            @RequestBody Map<String, String> payload) {

        String expiryDate = payload.get("expiryDate");
        if (expiryDate == null) {
            return ResponseEntity.badRequest().body("Expiry date is required");
        }

        boolean result = customerDocumentService.updateDocumentExpiryDate(basicNumber, documentName, expiryDate);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Helper method to get file extension based on content type
     */
    private String getFileExtension(String contentType) {
        if (contentType == null) {
            return ".bin";
        }

        switch (contentType.toLowerCase()) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "application/pdf":
                return ".pdf";
            case "application/msword":
                return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            default:
                return "." + contentType.substring(contentType.indexOf("/") + 1);
        }
    }
}