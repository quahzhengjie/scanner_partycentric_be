// DocumentController.java
package com.bkb.scanner.controller;

import com.bkb.scanner.dto.DocumentDTO;
import com.bkb.scanner.dto.DocumentValidationResultDTO;
import com.bkb.scanner.dto.UploadResultDTO;
import com.bkb.scanner.entity.DocumentCategory;
import com.bkb.scanner.entity.DocumentStatus;
import com.bkb.scanner.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@CrossOrigin
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/customer/{basicNumber}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsForCustomer(@PathVariable String basicNumber) {
        List<DocumentDTO> documents = documentService.getDocumentsForCustomer(basicNumber);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/customer/{basicNumber}/outstanding")
    public ResponseEntity<Boolean> hasOutstandingDocuments(@PathVariable String basicNumber) {
        boolean hasOutstanding = documentService.hasOutstandingDocuments(basicNumber);
        return ResponseEntity.ok(hasOutstanding);
    }

    @GetMapping("/customer/{basicNumber}/outstanding-list")
    public ResponseEntity<List<String>> getOutstandingDocumentsList(@PathVariable String basicNumber) {
        List<String> outstandingDocs = documentService.getOutstandingDocumentsList(basicNumber);
        return ResponseEntity.ok(outstandingDocs);
    }

    @PatchMapping("/customer/{basicNumber}/{filename}/status")
    public ResponseEntity<Void> updateDocumentStatus(
            @PathVariable String basicNumber,
            @PathVariable String filename,
            @RequestParam DocumentStatus status) {

        documentService.updateDocumentStatus(basicNumber, filename, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/customer/{basicNumber}/{filename}/upload")
    public ResponseEntity<UploadResultDTO> uploadDocument(
            @PathVariable String basicNumber,
            @PathVariable String filename,
            @RequestParam(required = false) String expiryDate,
            @RequestParam("file") MultipartFile file) {

        UploadResultDTO result = documentService.uploadDocument(basicNumber, filename, file, expiryDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/customer/{basicNumber}/category/{category}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByCategory(
            @PathVariable String basicNumber,
            @PathVariable DocumentCategory category) {

        List<DocumentDTO> documents = documentService.getDocumentsByCategory(basicNumber, category);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/customer/{basicNumber}/progress")
    public ResponseEntity<Integer> getDocumentUploadProgress(@PathVariable String basicNumber) {
        int progress = documentService.getDocumentUploadProgress(basicNumber);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/customer/{basicNumber}/validate")
    public ResponseEntity<DocumentValidationResultDTO> validateDocuments(@PathVariable String basicNumber) {
        DocumentValidationResultDTO result = documentService.validateDocuments(basicNumber);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<DocumentCategory>> getDocumentCategories() {
        List<DocumentCategory> categories = documentService.getDocumentCategories();
        return ResponseEntity.ok(categories);
    }
}