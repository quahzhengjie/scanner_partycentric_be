package com.bkb.scanner.dto;

import com.bkb.scanner.entity.DocumentCategory;
import com.bkb.scanner.entity.DocumentStatus;
import java.time.LocalDate;

public class AccountDocumentDTO {
    private Long id;
    private String documentName;
    private DocumentStatus uploadStatus;
    private LocalDate uploadDate;
    private String contentType;
    private String base64Content; // For UI display
    private DocumentCategory category;

    // Default constructor
    public AccountDocumentDTO() {
    }

    // All-args constructor
    public AccountDocumentDTO(Long id, String documentName, DocumentStatus uploadStatus,
                              LocalDate uploadDate, String contentType, String base64Content,
                              DocumentCategory category) {
        this.id = id;
        this.documentName = documentName;
        this.uploadStatus = uploadStatus;
        this.uploadDate = uploadDate;
        this.contentType = contentType;
        this.base64Content = base64Content;
        this.category = category;
    }

    // Builder pattern methods
    public static AccountDocumentDTOBuilder builder() {
        return new AccountDocumentDTOBuilder();
    }

    public static class AccountDocumentDTOBuilder {
        private Long id;
        private String documentName;
        private DocumentStatus uploadStatus;
        private LocalDate uploadDate;
        private String contentType;
        private String base64Content;
        private DocumentCategory category;

        public AccountDocumentDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AccountDocumentDTOBuilder documentName(String documentName) {
            this.documentName = documentName;
            return this;
        }

        public AccountDocumentDTOBuilder uploadStatus(DocumentStatus uploadStatus) {
            this.uploadStatus = uploadStatus;
            return this;
        }

        public AccountDocumentDTOBuilder uploadDate(LocalDate uploadDate) {
            this.uploadDate = uploadDate;
            return this;
        }

        public AccountDocumentDTOBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public AccountDocumentDTOBuilder base64Content(String base64Content) {
            this.base64Content = base64Content;
            return this;
        }

        public AccountDocumentDTOBuilder category(DocumentCategory category) {
            this.category = category;
            return this;
        }

        public AccountDocumentDTO build() {
            return new AccountDocumentDTO(id, documentName, uploadStatus, uploadDate,
                    contentType, base64Content, category);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public DocumentStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(DocumentStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }
}