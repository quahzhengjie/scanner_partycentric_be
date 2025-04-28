package com.bkb.scanner.dto;

import com.bkb.scanner.entity.DocumentCategory;
import com.bkb.scanner.entity.DocumentStatus;
import java.time.LocalDate;

public class DocumentDTO {
    private Long id;
    private String filename;
    private String uploadedFile; // Base64 encoded file content for UI
    private String uploadedName;
    private LocalDate expiryDate;
    private DocumentStatus status;
    private DocumentCategory category;
    private LocalDate uploadedDate;
    private String contentType;

    // Default constructor
    public DocumentDTO() {
    }

    // All-args constructor
    public DocumentDTO(Long id, String filename, String uploadedFile, String uploadedName,
                       LocalDate expiryDate, DocumentStatus status, DocumentCategory category,
                       LocalDate uploadedDate, String contentType) {
        this.id = id;
        this.filename = filename;
        this.uploadedFile = uploadedFile;
        this.uploadedName = uploadedName;
        this.expiryDate = expiryDate;
        this.status = status;
        this.category = category;
        this.uploadedDate = uploadedDate;
        this.contentType = contentType;
    }

    // Builder pattern methods
    public static DocumentDTOBuilder builder() {
        return new DocumentDTOBuilder();
    }

    public static class DocumentDTOBuilder {
        private Long id;
        private String filename;
        private String uploadedFile;
        private String uploadedName;
        private LocalDate expiryDate;
        private DocumentStatus status;
        private DocumentCategory category;
        private LocalDate uploadedDate;
        private String contentType;

        public DocumentDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DocumentDTOBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public DocumentDTOBuilder uploadedFile(String uploadedFile) {
            this.uploadedFile = uploadedFile;
            return this;
        }

        public DocumentDTOBuilder uploadedName(String uploadedName) {
            this.uploadedName = uploadedName;
            return this;
        }

        public DocumentDTOBuilder expiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public DocumentDTOBuilder status(DocumentStatus status) {
            this.status = status;
            return this;
        }

        public DocumentDTOBuilder category(DocumentCategory category) {
            this.category = category;
            return this;
        }

        public DocumentDTOBuilder uploadedDate(LocalDate uploadedDate) {
            this.uploadedDate = uploadedDate;
            return this;
        }

        public DocumentDTOBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public DocumentDTO build() {
            return new DocumentDTO(id, filename, uploadedFile, uploadedName, expiryDate,
                    status, category, uploadedDate, contentType);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(String uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getUploadedName() {
        return uploadedName;
    }

    public void setUploadedName(String uploadedName) {
        this.uploadedName = uploadedName;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }

    public LocalDate getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(LocalDate uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}