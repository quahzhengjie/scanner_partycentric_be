package com.bkb.scanner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAccountStatusDto {
    @NotBlank(message = "Status is required")
    private String status;
}