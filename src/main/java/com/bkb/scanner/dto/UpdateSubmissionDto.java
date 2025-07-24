package com.bkb.scanner.dto;

import com.bkb.scanner.entity.Submission.DocumentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSubmissionDto {
    @NotNull(message = "Status is required")
    private DocumentStatus status;

    private String comment;
}