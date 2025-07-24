package com.bkb.scanner.repository;

import com.bkb.scanner.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    Optional<Case> findByCaseId(String caseId);
    List<Case> findByAssignedTo(String assignedTo);
    List<Case> findByStatus(Case.CaseStatus status);
    List<Case> findByRiskLevel(Case.RiskLevel riskLevel);
    boolean existsByCaseId(String caseId);
}