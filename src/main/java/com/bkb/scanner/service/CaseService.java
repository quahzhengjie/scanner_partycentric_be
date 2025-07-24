package com.bkb.scanner.service;

import com.bkb.scanner.dto.*;
import com.bkb.scanner.entity.*;
import com.bkb.scanner.repository.*;
import com.bkb.scanner.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CaseService {

    private final CaseRepository caseRepository;
    private final AccountRepository accountRepository;
    private final SubmissionRepository submissionRepository;

    public List<Case> getAllCases(Case.CaseStatus status, String assignedTo, Case.RiskLevel riskLevel) {
        // Simple implementation - you can enhance with Specifications
        List<Case> cases = caseRepository.findAll();

        if (status != null) {
            cases = cases.stream().filter(c -> c.getStatus() == status).toList();
        }
        if (assignedTo != null) {
            cases = cases.stream().filter(c -> assignedTo.equals(c.getAssignedTo())).toList();
        }
        if (riskLevel != null) {
            cases = cases.stream().filter(c -> c.getRiskLevel() == riskLevel).toList();
        }

        return cases;
    }

    public Case getCaseByCaseId(String caseId) {
        return caseRepository.findByCaseId(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));
    }

    public Case createCase(CreateCaseDto dto, User currentUser) {
        Case newCase = new Case();
        newCase.setCaseId("CASE-" + System.currentTimeMillis());
        newCase.setStatus(Case.CaseStatus.DRAFT);
        newCase.setRiskLevel(dto.getRiskLevel() != null ? dto.getRiskLevel() : Case.RiskLevel.MEDIUM);
        newCase.setPriority(dto.getPriority() != null ? dto.getPriority() : Case.Priority.NORMAL);
        newCase.setAssignedTo(dto.getAssignedTo() != null ? dto.getAssignedTo() : currentUser.getName());

        EntityData entityData = new EntityData();
        entityData.setEntityName(dto.getEntityName());
        entityData.setEntityType(dto.getEntityType());
        newCase.setEntityData(entityData);

        // Add initial activity
        ActivityLog activity = new ActivityLog();
        activity.setActivityId("ACT-" + UUID.randomUUID());
        activity.setCaseEntity(newCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(currentUser.getName());
        activity.setAction("Case Created");
        newCase.getActivities().add(activity);

        return caseRepository.save(newCase);
    }

    public Case updateCase(String caseId, Case caseUpdate, User currentUser) {
        Case existingCase = getCaseByCaseId(caseId);

        // Update fields as needed
        if (caseUpdate.getStatus() != null) {
            existingCase.setStatus(caseUpdate.getStatus());
        }
        if (caseUpdate.getRiskLevel() != null) {
            existingCase.setRiskLevel(caseUpdate.getRiskLevel());
        }
        if (caseUpdate.getAssignedTo() != null) {
            existingCase.setAssignedTo(caseUpdate.getAssignedTo());
        }

        // Add activity log
        ActivityLog activity = new ActivityLog();
        activity.setActivityId("ACT-" + UUID.randomUUID());
        activity.setCaseEntity(existingCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(currentUser.getName());
        activity.setAction("Case Updated");
        existingCase.getActivities().add(activity);

        return caseRepository.save(existingCase);
    }

    public Case updateCaseStatus(String caseId, UpdateCaseStatusDto dto, User currentUser) {
        Case existingCase = getCaseByCaseId(caseId);
        existingCase.setStatus(dto.getStatus());

        // Add activity log
        ActivityLog activity = new ActivityLog();
        activity.setActivityId("ACT-" + UUID.randomUUID());
        activity.setCaseEntity(existingCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(currentUser.getName());
        activity.setAction("Status Changed to " + dto.getStatus());
        activity.setDetails(dto.getComment());
        existingCase.getActivities().add(activity);

        return caseRepository.save(existingCase);
    }

    public Case linkPartyToCase(String caseId, LinkPartyDto dto, User currentUser) {
        Case existingCase = getCaseByCaseId(caseId);

        CasePartyLink link = new CasePartyLink();
        link.setCaseEntity(existingCase);
        link.setPartyId(dto.getPartyId());
        link.setRelationshipType(dto.getRelationshipType());
        link.setOwnershipPercentage(dto.getOwnershipPercentage());
        link.setStartDate(LocalDate.now());  // Added LocalDate import for this
        link.setIsPrimary(existingCase.getRelatedPartyLinks().isEmpty());

        existingCase.getRelatedPartyLinks().add(link);

        // Add activity log
        ActivityLog activity = new ActivityLog();
        activity.setActivityId("ACT-" + UUID.randomUUID());
        activity.setCaseEntity(existingCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(currentUser.getName());
        activity.setAction("Party Linked");
        activity.setDetails("Linked party " + dto.getPartyId() + " as " + dto.getRelationshipType());
        existingCase.getActivities().add(activity);

        return caseRepository.save(existingCase);
    }

    public Case addSubmission(String caseId, String requirementId, CreateSubmissionDto dto, User currentUser) {
        Case existingCase = getCaseByCaseId(caseId);

        // Find or create document link
        CaseDocumentLink docLink = existingCase.getDocumentLinks().stream()
                .filter(dl -> dl.getRequirementId().equals(requirementId))
                .findFirst()
                .orElseGet(() -> {
                    CaseDocumentLink newLink = new CaseDocumentLink();
                    newLink.setLinkId("LNK-" + UUID.randomUUID());
                    newLink.setCaseEntity(existingCase);
                    newLink.setRequirementId(requirementId);
                    existingCase.getDocumentLinks().add(newLink);
                    return newLink;
                });

        // Create submission
        Submission submission = new Submission();
        submission.setSubmissionId("SUB-" + UUID.randomUUID());
        submission.setDocumentLink(docLink);
        submission.setMasterDocId(dto.getMasterDocId());
        submission.setStatus(dto.getStatus());
        submission.setSubmittedAt(dto.getSubmittedAt());
        submission.setSubmittedBy(dto.getSubmittedBy() != null ? dto.getSubmittedBy() : currentUser.getName());
        submission.setSubmissionMethod(dto.getSubmissionMethod());
        submission.setPublishedDate(dto.getPublishedDate());
        submission.setExpiryDate(dto.getExpiryDate());

        // Add initial comment if provided
        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            Comment comment = new Comment();
            comment.setCommentId("COM-" + UUID.randomUUID());
            comment.setSubmission(submission);
            comment.setAuthor(currentUser.getName());
            comment.setAuthorRole(currentUser.getRole().name());
            comment.setTimestamp(LocalDateTime.now());
            comment.setText(dto.getComment());
            submission.getComments().add(comment);
        }

        docLink.getSubmissions().add(submission);

        // Add activity log
        ActivityLog activity = new ActivityLog();
        activity.setActivityId("ACT-" + UUID.randomUUID());
        activity.setCaseEntity(existingCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(currentUser.getName());
        activity.setAction("Document Submitted");
        activity.setDetails("Submitted document for requirement " + requirementId);
        existingCase.getActivities().add(activity);

        return caseRepository.save(existingCase);
    }

    public Case updateSubmission(String caseId, String submissionId, UpdateSubmissionDto dto, User currentUser) {
        Case existingCase = getCaseByCaseId(caseId);

        // Find submission
        Submission submission = existingCase.getDocumentLinks().stream()
                .flatMap(dl -> dl.getSubmissions().stream())
                .filter(s -> s.getSubmissionId().equals(submissionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

        submission.setStatus(dto.getStatus());

        // Update review timestamps
        if (dto.getStatus() == Submission.DocumentStatus.PENDING_COMPLIANCE_VERIFICATION) {
            submission.setCheckerReviewedAt(LocalDateTime.now());
            submission.setCheckerReviewedBy(currentUser.getName());
        } else if (dto.getStatus() == Submission.DocumentStatus.VERIFIED) {
            submission.setComplianceReviewedAt(LocalDateTime.now());
            submission.setComplianceReviewedBy(currentUser.getName());
        }

        // Add comment if provided
        if (dto.getComment() != null && !dto.getComment().isEmpty()) {
            Comment comment = new Comment();
            comment.setCommentId("COM-" + UUID.randomUUID());
            comment.setSubmission(submission);
            comment.setAuthor(currentUser.getName());
            comment.setAuthorRole(currentUser.getRole().name());
            comment.setTimestamp(LocalDateTime.now());
            comment.setText(dto.getComment());
            submission.getComments().add(comment);
        }

        // Add activity log
        ActivityLog activity = new ActivityLog();
        activity.setActivityId("ACT-" + UUID.randomUUID());
        activity.setCaseEntity(existingCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(currentUser.getName());
        activity.setAction("Document Reviewed");
        activity.setDetails("Set submission " + submissionId + " to " + dto.getStatus());
        existingCase.getActivities().add(activity);

        return caseRepository.save(existingCase);
    }

    public Case createAccount(String caseId, CreateAccountDto dto, User currentUser) {
        Case existingCase = getCaseByCaseId(caseId);

        Account account = new Account();
        account.setAccountId("ACC-" + UUID.randomUUID());
        account.setCaseEntity(existingCase);
        account.setAccountType(dto.getAccountType());
        account.setCurrency(dto.getCurrency());
        account.setStatus("Proposed");
        account.setSignatureRules(dto.getSignatureRules());
        account.setPurpose(dto.getPurpose());

        // Set primary holder to first signatory
        if (!dto.getAuthorizedSignatories().isEmpty()) {
            account.setPrimaryHolderId(dto.getAuthorizedSignatories().get(0));
        }

        existingCase.getAccounts().add(account);

        // Add activity log
        ActivityLog activity = new ActivityLog();
        activity.setActivityId("ACT-" + UUID.randomUUID());
        activity.setCaseEntity(existingCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(currentUser.getName());
        activity.setAction("Account Proposed");
        activity.setDetails("Proposed " + dto.getAccountType());
        existingCase.getActivities().add(activity);

        return caseRepository.save(existingCase);
    }

    public Case updateAccountStatus(String caseId, String accountId, UpdateAccountStatusDto dto, User currentUser) {
        Case existingCase = getCaseByCaseId(caseId);

        Account account = existingCase.getAccounts().stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));

        account.setStatus(dto.getStatus());

        if ("Active".equals(dto.getStatus())) {
            account.setActivatedDate(LocalDateTime.now());
            account.setActivatedBy(currentUser.getName());
            account.setAccountNumber("ACC" + System.currentTimeMillis()); // Generate account number
        }

        // Add activity log
        ActivityLog activity = new ActivityLog();
        activity.setActivityId("ACT-" + UUID.randomUUID());
        activity.setCaseEntity(existingCase);
        activity.setTimestamp(LocalDateTime.now());
        activity.setActor(currentUser.getName());
        activity.setAction("Account Status Changed");
        activity.setDetails("Account " + accountId + " status changed to " + dto.getStatus());
        existingCase.getActivities().add(activity);

        return caseRepository.save(existingCase);
    }

    public Case addSubmission(String caseId, String requirementId, CreateSubmissionDto dto) {
        return addSubmission(caseId, requirementId, dto, null);
    }
}