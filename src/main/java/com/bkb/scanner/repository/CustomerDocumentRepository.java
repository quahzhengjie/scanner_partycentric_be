package com.bkb.scanner.repository;

import com.bkb.scanner.entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long> {

    /**
     * Find a document by customer basic number and document name
     */
    @Query("SELECT d FROM CustomerDocument d WHERE d.customer.basicNumber = :basicNumber AND d.documentName = :documentName")
    Optional<CustomerDocument> findByCustomerBasicNumberAndDocumentName(
            @Param("basicNumber") String basicNumber,
            @Param("documentName") String documentName);

    /**
     * Find all documents for a customer
     */
    @Query("SELECT d FROM CustomerDocument d WHERE d.customer.basicNumber = :basicNumber")
    List<CustomerDocument> findByCustomerBasicNumber(@Param("basicNumber") String basicNumber);

    /**
     * Check if a customer has specific documents uploaded
     */
    @Query("SELECT COUNT(d) = :count FROM CustomerDocument d " +
            "WHERE d.customer.basicNumber = :basicNumber " +
            "AND d.documentName IN :documentNames " +
            "AND d.uploadStatus = 'Uploaded'")
    boolean hasAllRequiredDocuments(
            @Param("basicNumber") String basicNumber,
            @Param("documentNames") List<String> documentNames,
            @Param("count") long count);

    @Query("SELECT d.documentName FROM CustomerDocument d " +
            "WHERE d.customer.basicNumber = :basicNumber " +
            "AND d.uploadStatus = 'Uploaded'")
    List<String> findUploadedDocumentNames(@Param("basicNumber") String basicNumber);
}