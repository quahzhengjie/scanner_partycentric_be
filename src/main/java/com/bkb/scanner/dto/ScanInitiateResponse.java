package com.bkb.scanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScanInitiateResponse {
    private String scanId;
    private Integer estimatedTime;
    private String message;
}