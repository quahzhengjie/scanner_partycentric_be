package com.bkb.scanner.dto;

import java.time.LocalDate;

public class CustomerDocumentDTO {
    private Long id;
    private String documentName;
    private String uploadStatus;
    private LocalDate uploadDate;
    private LocalDate expiryDate; // Added field
    private String customerBasicNumber;
    private String customerName;
    private String category;
    private String contentType;
    private byte[] fileContent;

    // Default constructor
    public CustomerDocumentDTO() {
    }

    // Full constructor
    public CustomerDocumentDTO(Long id, String documentName, String uploadStatus,
                               LocalDate uploadDate, LocalDate expiryDate, String customerBasicNumber,
                               String customerName, String category,
                               String contentType, byte[] fileContent) {
        this.id = id;
        this.documentName = documentName;
        this.uploadStatus = uploadStatus;
        this.uploadDate = uploadDate;
        this.expiryDate = expiryDate; // Initialize the new field
        this.customerBasicNumber = customerBasicNumber;
        this.customerName = customerName;
        this.category = category;
        this.contentType = contentType;
        this.fileContent = fileContent;
    }

    // Builder constructor for easier creation
    public static CustomerDocumentDTOBuilder builder() {
        return new CustomerDocumentDTOBuilder();
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

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    // New getter and setter for expiryDate
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCustomerBasicNumber() {
        return customerBasicNumber;
    }

    public void setCustomerBasicNumber(String customerBasicNumber) {
        this.customerBasicNumber = customerBasicNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    // Builder pattern implementation
    public static class CustomerDocumentDTOBuilder {
        private Long id;
        private String documentName;
        private String uploadStatus;
        private LocalDate uploadDate;
        private LocalDate expiryDate; // Added field
        private String customerBasicNumber;
        private String customerName;
        private String category;
        private String contentType;
        private byte[] fileContent;

        public CustomerDocumentDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CustomerDocumentDTOBuilder documentName(String documentName) {
            this.documentName = documentName;
            return this;
        }

        public CustomerDocumentDTOBuilder uploadStatus(String uploadStatus) {
            this.uploadStatus = uploadStatus;
            return this;
        }

        public CustomerDocumentDTOBuilder uploadDate(LocalDate uploadDate) {
            this.uploadDate = uploadDate;
            return this;
        }

        // New builder method for expiryDate
        public CustomerDocumentDTOBuilder expiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public CustomerDocumentDTOBuilder customerBasicNumber(String customerBasicNumber) {
            this.customerBasicNumber = customerBasicNumber;
            return this;
        }

        public CustomerDocumentDTOBuilder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public CustomerDocumentDTOBuilder category(String category) {
            this.category = category;
            return this;
        }

        public CustomerDocumentDTOBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public CustomerDocumentDTOBuilder fileContent(byte[] fileContent) {
            this.fileContent = fileContent;
            return this;
        }

        public CustomerDocumentDTO build() {
            return new CustomerDocumentDTO(
                    id, documentName, uploadStatus, uploadDate, expiryDate,
                    customerBasicNumber, customerName, category,
                    contentType, fileContent);
        }
    }
}