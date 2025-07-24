package com.bkb.scanner.controller;

import com.bkb.scanner.dto.*;
import com.bkb.scanner.entity.ScanProfile;
import com.bkb.scanner.entity.User;
import com.bkb.scanner.service.ScanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scan")
@RequiredArgsConstructor
// Option 2: If you must keep @CrossOrigin, use specific origins:

public class ScanController {

    private final ScanService scanService;

    @GetMapping("/profiles")
    public ResponseEntity<List<ScanProfile>> getScanProfiles() {
        return ResponseEntity.ok(scanService.getAvailableProfiles());
    }

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<ScanInitiateResponse>> initiateScan(
            @Valid @RequestBody ScanInitiateRequest request,
            @AuthenticationPrincipal User currentUser) {
        ScanInitiateResponse response = scanService.initiateScan(
                request.getCaseId(),
                request.getRequirementId(),
                request.getDocType(),
                request.getPartyId(),
                request.getScanProfile()
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{scanId}")
    public ResponseEntity<ApiResponse<ScanStatusResponse>> getScanStatus(@PathVariable String scanId) {
        ScanStatusResponse status = scanService.getScanStatus(scanId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    @PostMapping("/cancel/{scanId}")
    public ResponseEntity<ApiResponse<Void>> cancelScan(
            @PathVariable String scanId,
            @AuthenticationPrincipal User currentUser) {
        scanService.cancelScan(scanId);
        return ResponseEntity.ok(ApiResponse.success("Scan cancelled", null));
    }
}