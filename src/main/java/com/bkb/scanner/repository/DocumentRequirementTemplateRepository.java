package com.bkb.scanner.repository;

import com.bkb.scanner.entity.DocumentRequirementTemplate;
import com.bkb.scanner.entity.EntityData;
import com.bkb.scanner.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRequirementTemplateRepository extends JpaRepository<DocumentRequirementTemplate, Long> {

    List<DocumentRequirementTemplate> findByRequirementTypeAndEntityTypeAndIsActiveTrue(
            DocumentRequirementTemplate.RequirementType type,
            EntityData.EntityType entityType
    );

    List<DocumentRequirementTemplate> findByRequirementTypeAndResidencyStatusAndIsActiveTrue(
            DocumentRequirementTemplate.RequirementType type,
            String residencyStatus
    );

    List<DocumentRequirementTemplate> findByRequirementTypeAndRiskLevelAndIsActiveTrue(
            DocumentRequirementTemplate.RequirementType type,
            Case.RiskLevel riskLevel
    );

    List<DocumentRequirementTemplate> findByRequirementTypeAndIsActiveTrue(
            DocumentRequirementTemplate.RequirementType type
    );

    List<DocumentRequirementTemplate> findByRequirementTypeAndAccountTypeAndIsActiveTrue(
            DocumentRequirementTemplate.RequirementType type,
            String accountType
    );
}