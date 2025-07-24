package com.bkb.scanner.repository;

import com.bkb.scanner.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByDocId(String docId);
    List<Document> findByOwnerPartyId(String ownerPartyId);
    List<Document> findByDocType(String docType);
    boolean existsByDocId(String docId);
}