// DocumentRequirementsController.java
package com.bkb.scanner.controller;

import com.bkb.scanner.dto.DocumentDTO;
import com.bkb.scanner.dto.SelectOptionDTO;
import com.bkb.scanner.entity.CustomerType;
import com.bkb.scanner.entity.RiskRating;
import com.bkb.scanner.service.DocumentRequirementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/document-requirements")
@CrossOrigin
public class DocumentRequirementsController {

    private final DocumentRequirementsService documentRequirementsService;

    @Autowired
    public DocumentRequirementsController(DocumentRequirementsService documentRequirementsService) {
        this.documentRequirementsService = documentRequirementsService;
    }

    @GetMapping("/required-documents")
    public ResponseEntity<List<DocumentDTO>> getRequiredDocuments(
            @RequestParam CustomerType customerType,
            @RequestParam(required = false, defaultValue = "false") Boolean isPEP,
            @RequestParam(required = false, defaultValue = "Singapore") String registrationCountry,
            @RequestParam(required = false, defaultValue = "Low") RiskRating riskRating) {

        List<DocumentDTO> documents = documentRequirementsService.getRequiredDocuments(
                customerType, isPEP, registrationCountry, riskRating);

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/high-risk-jurisdiction/{country}")
    public ResponseEntity<Boolean> isHighRiskJurisdiction(@PathVariable String country) {
        boolean isHighRisk = documentRequirementsService.isHighRiskJurisdiction(country);
        return ResponseEntity.ok(isHighRisk);
    }

    @GetMapping("/customer-type-options")
    public ResponseEntity<List<SelectOptionDTO>> getCustomerTypeOptions() {
        return ResponseEntity.ok(documentRequirementsService.getCustomerTypeOptions());
    }
}