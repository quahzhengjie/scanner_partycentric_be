package com.bkb.scanner.controller;

import com.bkb.scanner.dto.*;
import com.bkb.scanner.entity.Case;
import com.bkb.scanner.entity.User;
import com.bkb.scanner.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @GetMapping
    public ResponseEntity<List<Case>> getAllCases(
            @RequestParam(required = false) Case.CaseStatus status,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false) Case.RiskLevel riskLevel) {
        return ResponseEntity.ok(caseService.getAllCases(status, assignedTo, riskLevel));
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<Case> getCaseById(@PathVariable String caseId) {
        return ResponseEntity.ok(caseService.getCaseByCaseId(caseId));
    }

    @PostMapping
    public ResponseEntity<Case> createCase(
            @Valid @RequestBody CreateCaseDto createCaseDto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(caseService.createCase(createCaseDto, currentUser));
    }

    @PutMapping("/{caseId}")
    public ResponseEntity<Case> updateCase(
            @PathVariable String caseId,
            @RequestBody Case caseUpdate,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(caseService.updateCase(caseId, caseUpdate, currentUser));
    }

    @PatchMapping("/{caseId}/status")
    public ResponseEntity<Case> updateCaseStatus(
            @PathVariable String caseId,
            @RequestBody UpdateCaseStatusDto statusUpdate,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(caseService.updateCaseStatus(caseId, statusUpdate, currentUser));
    }

    @PostMapping("/{caseId}/parties")
    public ResponseEntity<Case> linkPartyToCase(
            @PathVariable String caseId,
            @RequestBody LinkPartyDto linkPartyDto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(caseService.linkPartyToCase(caseId, linkPartyDto, currentUser));
    }

    @PostMapping("/{caseId}/submissions")
    public ResponseEntity<Case> addSubmission(
            @PathVariable String caseId,
            @RequestParam String requirementId,
            @Valid @RequestBody CreateSubmissionDto submissionDto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(caseService.addSubmission(caseId, requirementId, submissionDto, currentUser));
    }

    @PutMapping("/{caseId}/submissions/{submissionId}")
    public ResponseEntity<Case> updateSubmission(
            @PathVariable String caseId,
            @PathVariable String submissionId,
            @Valid @RequestBody UpdateSubmissionDto updateDto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(caseService.updateSubmission(caseId, submissionId, updateDto, currentUser));
    }

    @PostMapping("/{caseId}/accounts")
    public ResponseEntity<Case> createAccount(
            @PathVariable String caseId,
            @Valid @RequestBody CreateAccountDto accountDto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(caseService.createAccount(caseId, accountDto, currentUser));
    }

    @PatchMapping("/{caseId}/accounts/{accountId}/status")
    public ResponseEntity<Case> updateAccountStatus(
            @PathVariable String caseId,
            @PathVariable String accountId,
            @RequestBody UpdateAccountStatusDto statusDto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(caseService.updateAccountStatus(caseId, accountId, statusDto, currentUser));
    }

}