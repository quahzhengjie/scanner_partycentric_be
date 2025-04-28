package com.bkb.scanner.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customer_documents")
public class CustomerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "upload_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus uploadStatus = DocumentStatus.Pending;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentCategory category;

    @Lob
    @Column(name = "file_content", columnDefinition = "LONGBLOB")
    private byte[] fileContent;

    @Column(name = "content_type")
    private String contentType;

    // Constructors
    public CustomerDocument() {
    }

    public CustomerDocument(Long id, String documentName, DocumentStatus uploadStatus,
                            LocalDate uploadDate, LocalDate expiryDate, Customer customer,
                            DocumentCategory category, byte[] fileContent, String contentType) {
        this.id = id;
        this.documentName = documentName;
        this.uploadStatus = uploadStatus;
        this.uploadDate = uploadDate;
        this.expiryDate = expiryDate;
        this.customer = customer;
        this.category = category;
        this.fileContent = fileContent;
        this.contentType = contentType;
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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "CustomerDocument{" +
                "id=" + id +
                ", documentName='" + documentName + '\'' +
                ", uploadStatus=" + uploadStatus +
                ", uploadDate=" + uploadDate +
                ", expiryDate=" + expiryDate +
                ", customer=" + (customer != null ? customer.getBasicNumber() : "null") +
                ", category=" + category +
                ", contentType='" + contentType + '\'' +
                ", hasFileContent=" + (fileContent != null && fileContent.length > 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerDocument that = (CustomerDocument) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return documentName != null ? documentName.equals(that.documentName) : that.documentName == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (documentName != null ? documentName.hashCode() : 0);
        return result;
    }
}