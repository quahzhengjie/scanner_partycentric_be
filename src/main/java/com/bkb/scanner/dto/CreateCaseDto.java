package com.bkb.scanner.dto;

import com.bkb.scanner.entity.EntityData.EntityType;
import com.bkb.scanner.entity.Case.RiskLevel;
import com.bkb.scanner.entity.Case.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCaseDto {
    @NotBlank(message = "Entity name is required")
    private String entityName;

    @NotNull(message = "Entity type is required")
    private EntityType entityType;

    private String assignedTo;
    private Priority priority;
    private RiskLevel riskLevel;
}