package com.bkb.scanner.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "account_documents")
public class AccountDocument {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentCategory category;

    @Lob
    @Column(name = "file_content", columnDefinition = "LONGBLOB")
    private byte[] fileContent;

    @Column(name = "content_type")
    private String contentType;

    // Constructors
    public AccountDocument() {
    }

    public AccountDocument(Long id, String documentName, DocumentStatus uploadStatus,
                           LocalDate uploadDate, Account account, DocumentCategory category,
                           byte[] fileContent, String contentType) {
        this.id = id;
        this.documentName = documentName;
        this.uploadStatus = uploadStatus;
        this.uploadDate = uploadDate;
        this.account = account;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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
}