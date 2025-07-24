package com.bkb.scanner.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScanStatusResponse {
    private String status;
    private Integer progress;
    private String documentId;
    private String error;
}