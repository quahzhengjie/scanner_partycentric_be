// AccountDocumentRepository.java
package com.bkb.scanner.repository;

import com.bkb.scanner.entity.AccountDocument;
import com.bkb.scanner.entity.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountDocumentRepository extends JpaRepository<AccountDocument, Long> {

    List<AccountDocument> findByAccountAccountNumber(String accountNumber);

    Optional<AccountDocument> findByAccountAccountNumberAndDocumentName(String accountNumber, String documentName);

    List<AccountDocument> findByAccountAccountNumberAndUploadStatus(String accountNumber, DocumentStatus status);

    @Query("SELECT COUNT(ad) = 0 FROM AccountDocument ad " +
            "WHERE ad.account.accountNumber = :accountNumber AND ad.documentName IN :requiredDocs " +
            "AND ad.uploadStatus = 'Pending'")
    boolean hasAllRequiredDocuments(
            @Param("accountNumber") String accountNumber,
            @Param("requiredDocs") List<String> requiredDocs);

    @Query("SELECT ad.documentName FROM AccountDocument ad " +
            "WHERE ad.account.accountNumber = :accountNumber AND ad.documentName IN :requiredDocs " +
            "AND ad.uploadStatus = 'Pending'")
    List<String> getMissingDocuments(
            @Param("accountNumber") String accountNumber,
            @Param("requiredDocs") List<String> requiredDocs);
}