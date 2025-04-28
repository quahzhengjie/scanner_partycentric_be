// DocumentRepository.java
package com.bkb.scanner.repository;

import com.bkb.scanner.entity.Document;
import com.bkb.scanner.entity.DocumentCategory;
import com.bkb.scanner.entity.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByCustomerBasicNumber(String basicNumber);

    List<Document> findByCustomerBasicNumberAndStatus(String basicNumber, DocumentStatus status);

    List<Document> findByCustomerBasicNumberAndCategory(String basicNumber, DocumentCategory category);

    @Query("SELECT COUNT(d) > 0 FROM Document d WHERE d.customer.basicNumber = :basicNumber AND d.status = 'Pending'")
    boolean hasOutstandingDocuments(@Param("basicNumber") String basicNumber);

    @Query("SELECT d.filename FROM Document d WHERE d.customer.basicNumber = :basicNumber AND d.status = 'Pending'")
    List<String> getOutstandingDocumentsList(@Param("basicNumber") String basicNumber);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.customer.basicNumber = :basicNumber")
    int countDocumentsByCustomer(@Param("basicNumber") String basicNumber);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.customer.basicNumber = :basicNumber AND d.status = 'Uploaded'")
    int countUploadedDocumentsByCustomer(@Param("basicNumber") String basicNumber);
}