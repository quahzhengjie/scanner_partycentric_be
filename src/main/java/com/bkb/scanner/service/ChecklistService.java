package com.bkb.scanner.service;

import com.bkb.scanner.entity.*;
import com.bkb.scanner.repository.PartyRepository;
import com.bkb.scanner.repository.DocumentRequirementTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChecklistService {

    private final PartyRepository partyRepository;
    private final DocumentRequirementTemplateRepository requirementTemplateRepository;

    public List<ChecklistSection> generateChecklist(Case caseData) {
        List<ChecklistSection> sections = new ArrayList<>();
        List<Party> allParties = partyRepository.findAll();

        // Entity documents section
        List<ChecklistItem> entityDocs = generateEntityDocuments(caseData);
        if (!entityDocs.isEmpty()) {
            sections.add(new ChecklistSection(
                    caseData.getEntityData().getEntityName() + " (Entity Docs)",
                    entityDocs
            ));
        }

        // Party documents sections
        for (CasePartyLink partyLink : caseData.getRelatedPartyLinks()) {
            Party party = allParties.stream()
                    .filter(p -> p.getPartyId().equals(partyLink.getPartyId()))
                    .findFirst()
                    .orElse(null);

            if (party != null) {
                List<ChecklistItem> partyDocs = generatePartyDocuments(caseData, party, partyLink);
                if (!partyDocs.isEmpty()) {
                    sections.add(new ChecklistSection(
                            party.getName() + " (" + partyLink.getRelationshipType() + ")",
                            partyDocs
                    ));
                }
            }
        }

        return sections;
    }

    private List<ChecklistItem> generateEntityDocuments(Case caseData) {
        List<ChecklistItem> items = new ArrayList<>();
        EntityData.EntityType entityType = caseData.getEntityData().getEntityType();

        // Get entity-specific documents from database
        List<DocumentRequirementTemplate> entityTemplates =
                requirementTemplateRepository.findByRequirementTypeAndEntityTypeAndIsActiveTrue(
                        DocumentRequirementTemplate.RequirementType.ENTITY_DOCUMENT,
                        entityType
                );

        // Add entity documents
        int index = 0;
        for (DocumentRequirementTemplate template : entityTemplates) {
            items.add(createChecklistItem(caseData, template, "req-entity-" + index, "ENTITY"));
            index++;
        }

        // Get bank forms based on entity type
        String formCategory = entityType == EntityData.EntityType.INDIVIDUAL_ACCOUNT
                ? "INDIVIDUAL" : "CORPORATE";

        List<DocumentRequirementTemplate> bankForms =
                requirementTemplateRepository.findByRequirementTypeAndAccountTypeAndIsActiveTrue(
                        DocumentRequirementTemplate.RequirementType.BANK_FORM,
                        formCategory
                );

        // Add bank forms
        index = 0;
        for (DocumentRequirementTemplate template : bankForms) {
            items.add(createChecklistItem(caseData, template, "req-forms-" + index, "ENTITY"));
            index++;
        }

        // Get risk-based documents
        List<DocumentRequirementTemplate> riskDocs =
                requirementTemplateRepository.findByRequirementTypeAndRiskLevelAndIsActiveTrue(
                        DocumentRequirementTemplate.RequirementType.RISK_BASED,
                        caseData.getRiskLevel()
                );

        // Add risk-based documents
        index = 0;
        for (DocumentRequirementTemplate template : riskDocs) {
            items.add(createChecklistItem(caseData, template, "req-risk-" + index, "ENTITY"));
            index++;
        }

        // Sort by sortOrder
        return items.stream()
                .sorted(Comparator.comparing(ChecklistItem::getSortOrder))
                .collect(Collectors.toList());
    }

    private List<ChecklistItem> generatePartyDocuments(Case caseData, Party party, CasePartyLink partyLink) {
        List<ChecklistItem> items = new ArrayList<>();

        String residencyStatus = party.getResidencyStatus();
        if (residencyStatus == null) {
            residencyStatus = "Singaporean/PR";
        }

        // Get individual documents based on residency status
        List<DocumentRequirementTemplate> individualTemplates =
                requirementTemplateRepository.findByRequirementTypeAndResidencyStatusAndIsActiveTrue(
                        DocumentRequirementTemplate.RequirementType.INDIVIDUAL_DOCUMENT,
                        residencyStatus
                );

        int index = 0;
        for (DocumentRequirementTemplate template : individualTemplates) {
            items.add(createChecklistItem(
                    caseData,
                    template,
                    "req-party-" + party.getPartyId() + "-" + index,
                    party.getPartyId()
            ));
            index++;
        }

        // Sort by sortOrder
        return items.stream()
                .sorted(Comparator.comparing(ChecklistItem::getSortOrder))
                .collect(Collectors.toList());
    }

    private ChecklistItem createChecklistItem(Case caseData, DocumentRequirementTemplate template,
                                              String requirementId, String ownerPartyId) {
        ChecklistItem item = new ChecklistItem();
        item.setId(requirementId);
        item.setName(template.getName());
        item.setRequired(template.getRequired());
        item.setDescription(template.getDescription());
        item.setValidityMonths(template.getValidityMonths());
        item.setOwnerPartyId(ownerPartyId);
        item.setSortOrder(template.getSortOrder());

        // Find corresponding document link and submissions
        CaseDocumentLink link = caseData.getDocumentLinks().stream()
                .filter(dl -> dl.getRequirementId().equals(requirementId))
                .findFirst()
                .orElse(null);

        if (link != null && !link.getSubmissions().isEmpty()) {
            item.setSubmissions(link.getSubmissions());
            Submission latestSubmission = link.getSubmissions().get(link.getSubmissions().size() - 1);
            item.setStatus(latestSubmission.getStatus());
        } else {
            item.setStatus(Submission.DocumentStatus.MISSING);
            item.setSubmissions(new ArrayList<>());
        }

        return item;
    }

    // Updated ChecklistItem class
    public static class ChecklistItem {
        private String id;
        private String name;
        private boolean required;
        private String description;
        private Integer validityMonths;
        private Submission.DocumentStatus status;
        private String ownerPartyId;
        private List<Submission> submissions;
        private Integer sortOrder;

        public ChecklistItem() {
            this.submissions = new ArrayList<>();
            this.sortOrder = 0;
        }

        // All getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getValidityMonths() { return validityMonths; }
        public void setValidityMonths(Integer validityMonths) { this.validityMonths = validityMonths; }

        public Submission.DocumentStatus getStatus() { return status; }
        public void setStatus(Submission.DocumentStatus status) { this.status = status; }

        public String getOwnerPartyId() { return ownerPartyId; }
        public void setOwnerPartyId(String ownerPartyId) { this.ownerPartyId = ownerPartyId; }

        public List<Submission> getSubmissions() { return submissions; }
        public void setSubmissions(List<Submission> submissions) { this.submissions = submissions; }

        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }

    public static class ChecklistSection {
        private String title;
        private List<ChecklistItem> items;

        public ChecklistSection(String title, List<ChecklistItem> items) {
            this.title = title;
            this.items = items;
        }

        public String getTitle() { return title; }
        public List<ChecklistItem> getItems() { return items; }
    }
}