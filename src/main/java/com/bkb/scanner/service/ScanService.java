package com.bkb.scanner.service;

import com.bkb.scanner.dto.*;
import com.bkb.scanner.entity.*;
import com.bkb.scanner.repository.*;
import com.bkb.scanner.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScanService {

    private final ScanQueueRepository scanQueueRepository;
    private final ScanProfileRepository scanProfileRepository;
    private final DocumentRepository documentRepository;
    private final CaseService caseService;

    @Value("${naps2.exe.path}")
    private String naps2ExePath;

    @Value("${scan.temp.directory}")
    private String scanTempDirectory;

    public ScanInitiateResponse initiateScan(String caseId, String requirementId,
                                             String docType, String partyId, String scanProfile) {
        if (scanProfile == null || scanProfile.isEmpty()) {
            scanProfile = "Default";
        }

        ScanQueue scanQueue = new ScanQueue();
        scanQueue.setScanId("SCAN-" + UUID.randomUUID());
        scanQueue.setCaseId(caseId);
        scanQueue.setRequirementId(requirementId);
        scanQueue.setDocType(docType);
        scanQueue.setPartyId(partyId);
        scanQueue.setScanProfile(scanProfile);
        scanQueue.setStatus(ScanQueue.ScanStatus.PENDING);
        scanQueue.setProgress(0);

        scanQueueRepository.save(scanQueue);

        // Start async scanning process
        // performScan(scanQueue.getScanId());

        return new ScanInitiateResponse(
                scanQueue.getScanId(),
                getEstimatedTime(scanProfile),
                "Scan initiated successfully"
        );
    }

    public ScanStatusResponse getScanStatus(String scanId) {
        ScanQueue scanQueue = scanQueueRepository.findByScanId(scanId)
                .orElseThrow(() -> new ResourceNotFoundException("Scan not found"));

        return ScanStatusResponse.builder()
                .status(scanQueue.getStatus().toString().toLowerCase())
                .progress(scanQueue.getProgress())
                .documentId(scanQueue.getDocumentId())
                .error(scanQueue.getErrorMessage())
                .build();
    }

    public void cancelScan(String scanId) {
        ScanQueue scanQueue = scanQueueRepository.findByScanId(scanId)
                .orElseThrow(() -> new ResourceNotFoundException("Scan not found"));

        if (scanQueue.getStatus() == ScanQueue.ScanStatus.PENDING ||
                scanQueue.getStatus() == ScanQueue.ScanStatus.SCANNING) {
            scanQueue.setStatus(ScanQueue.ScanStatus.FAILED);
            scanQueue.setErrorMessage("Scan cancelled by user");
            scanQueueRepository.save(scanQueue);
        }
    }

    public List<ScanProfile> getAvailableProfiles() {
        return scanProfileRepository.findByIsActiveTrue();
    }

    private int getEstimatedTime(String profileName) {
        if (profileName != null && profileName.toLowerCase().contains("high")) {
            return 60;
        }
        return 30;
    }
}