package com.bkb.scanner.dto;

import com.bkb.scanner.entity.Submission.DocumentStatus;
import com.bkb.scanner.entity.Submission.SubmissionMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateSubmissionDto {
    @NotBlank(message = "Master document ID is required")
    private String masterDocId;

    private DocumentStatus status = DocumentStatus.PENDING_CHECKER_VERIFICATION;
    private String submittedBy;
    private LocalDateTime submittedAt = LocalDateTime.now();
    private SubmissionMethod submissionMethod = SubmissionMethod.UPLOAD;
    private LocalDate publishedDate;
    private LocalDate expiryDate;
    private String comment;
}