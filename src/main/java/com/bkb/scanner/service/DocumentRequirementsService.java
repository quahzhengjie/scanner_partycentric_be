// DocumentRequirementsService.java
package com.bkb.scanner.service;

import com.bkb.scanner.dto.DocumentDTO;
import com.bkb.scanner.dto.SelectOptionDTO;
import com.bkb.scanner.entity.CustomerType;
import com.bkb.scanner.entity.RiskRating;

import java.util.List;

public interface DocumentRequirementsService {

    List<DocumentDTO> getRequiredDocuments(
            CustomerType customerType,
            Boolean isPEP,
            String registrationCountry,
            RiskRating riskRating);

    boolean isHighRiskJurisdiction(String country);

    List<SelectOptionDTO> getCustomerTypeOptions();
}
