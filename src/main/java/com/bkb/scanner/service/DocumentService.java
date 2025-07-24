package com.bkb.scanner.service;

import com.bkb.scanner.entity.Document;
import com.bkb.scanner.repository.DocumentRepository;
import com.bkb.scanner.exception.ResourceNotFoundException;
import com.bkb.scanner.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    public Document uploadDocument(MultipartFile file, String ownerPartyId, String docType,
                                   String issueDate, String expiryDate) {
        try {
            Document document = new Document();
            document.setDocId("DOC-" + UUID.randomUUID());
            document.setOwnerPartyId(ownerPartyId);
            document.setDocType(docType);
            document.setFileName(file.getOriginalFilename());
            document.setFileSize(file.getSize());
            document.setMimeType(file.getContentType());
            document.setFileData(file.getBytes());
            document.setUploadedBy("System"); // Should be current user
            document.setCategory(determineCategory(docType));

            if (issueDate != null) {
                document.setIssueDate(LocalDate.parse(issueDate));
            }
            if (expiryDate != null) {
                document.setExpiryDate(LocalDate.parse(expiryDate));
            }

            return documentRepository.save(document);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload document: " + e.getMessage());
        }
    }

    public Document getDocumentByDocId(String docId) {
        return documentRepository.findByDocId(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + docId));
    }

    public void deleteDocument(String docId) {
        Document document = getDocumentByDocId(docId);
        documentRepository.delete(document);
    }

    private Document.DocumentCategory determineCategory(String docType) {
        String lowerDocType = docType.toLowerCase();

        if (lowerDocType.contains("passport") || lowerDocType.contains("identity") ||
                lowerDocType.contains("nric") || lowerDocType.contains("birth certificate")) {
            return Document.DocumentCategory.IDENTITY;
        } else if (lowerDocType.contains("address") || lowerDocType.contains("utility")) {
            return Document.DocumentCategory.ADDRESS;
        } else if (lowerDocType.contains("financial") || lowerDocType.contains("income")) {
            return Document.DocumentCategory.FINANCIAL;
        } else if (lowerDocType.contains("incorporation") || lowerDocType.contains("certificate")) {
            return Document.DocumentCategory.CORPORATE;
        } else if (lowerDocType.contains("agreement") || lowerDocType.contains("contract")) {
            return Document.DocumentCategory.LEGAL;
        }

        return Document.DocumentCategory.OTHER;
    }
}