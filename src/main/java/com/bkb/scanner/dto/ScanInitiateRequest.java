package com.bkb.scanner.dto;

import lombok.Data;

@Data
public class ScanInitiateRequest {
    private String caseId;
    private String requirementId;
    private String docType;
    private String partyId;
    private String scanProfile;
}