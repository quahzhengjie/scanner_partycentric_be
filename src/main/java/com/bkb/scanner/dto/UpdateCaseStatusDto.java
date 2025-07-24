package com.bkb.scanner.dto;

import com.bkb.scanner.entity.Case.CaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCaseStatusDto {
    @NotNull(message = "Status is required")
    private CaseStatus status;

    private String comment;
}