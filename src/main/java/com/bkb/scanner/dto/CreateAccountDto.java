package com.bkb.scanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateAccountDto {
    @NotBlank(message = "Account type is required")
    private String accountType;

    private String currency = "SGD";

    @NotEmpty(message = "At least one authorized signatory is required")
    private List<String> authorizedSignatories;

    private String signatureRules;
    private String purpose;
}