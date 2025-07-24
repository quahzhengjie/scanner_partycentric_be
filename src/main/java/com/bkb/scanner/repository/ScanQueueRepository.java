package com.bkb.scanner.repository;

import com.bkb.scanner.entity.ScanQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScanQueueRepository extends JpaRepository<ScanQueue, Long> {
    Optional<ScanQueue> findByScanId(String scanId);
    List<ScanQueue> findByStatus(ScanQueue.ScanStatus status);
    List<ScanQueue> findByCaseId(String caseId);
    boolean existsByScanId(String scanId);
}