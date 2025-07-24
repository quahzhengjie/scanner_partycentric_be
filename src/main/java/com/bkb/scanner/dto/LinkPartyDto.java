package com.bkb.scanner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LinkPartyDto {
    @NotBlank(message = "Party ID is required")
    private String partyId;

    @NotBlank(message = "Relationship type is required")
    private String relationshipType;

    private BigDecimal ownershipPercentage;
}