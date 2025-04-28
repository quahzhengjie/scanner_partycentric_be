package com.bkb.scanner.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "uploaded_name")
    private String uploadedName;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.Pending;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentCategory category;

    @Lob
    @Column(name = "file_content")
    private byte[] fileContent;

    @Column(name = "content_type")
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "uploaded_date")
    private LocalDate uploadedDate;

    // Constructors
    public Document() {
    }

    public Document(Long id, String filename, String uploadedName, LocalDate expiryDate,
                    DocumentStatus status, DocumentCategory category, byte[] fileContent,
                    String contentType, Customer customer, LocalDate uploadedDate) {
        this.id = id;
        this.filename = filename;
        this.uploadedName = uploadedName;
        this.expiryDate = expiryDate;
        this.status = status;
        this.category = category;
        this.fileContent = fileContent;
        this.contentType = contentType;
        this.customer = customer;
        this.uploadedDate = uploadedDate;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDate getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(LocalDate uploadedDate) {
        this.uploadedDate = uploadedDate;
    }
}