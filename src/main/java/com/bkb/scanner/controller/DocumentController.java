package com.bkb.scanner.controller;

import com.bkb.scanner.entity.Document;
import com.bkb.scanner.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerPartyId") String ownerPartyId,
            @RequestParam("docType") String docType,
            @RequestParam(required = false) String issueDate,
            @RequestParam(required = false) String expiryDate) {
        Document document = documentService.uploadDocument(file, ownerPartyId, docType, issueDate, expiryDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @GetMapping("/{docId}")
    public ResponseEntity<Document> getDocument(@PathVariable String docId) {
        return ResponseEntity.ok(documentService.getDocumentByDocId(docId));
    }

    @GetMapping("/{docId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String docId) {
        Document document = documentService.getDocumentByDocId(docId);
        ByteArrayResource resource = new ByteArrayResource(document.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .contentLength(document.getFileSize())
                .body(resource);
    }

    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String docId) {
        documentService.deleteDocument(docId);
        return ResponseEntity.noContent().build();
    }
}