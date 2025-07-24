package com.bkb.scanner.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/mockcases")
public class MockCaseController {

    @GetMapping
    public List<Map<String, Object>> getAllCases() {
        List<Map<String, Object>> cases = new ArrayList<>();

        // Mock case 1
        Map<String, Object> case1 = new HashMap<>();
        case1.put("caseId", "CASE-2025-001");
        case1.put("status", "Draft");
        case1.put("riskLevel", "Medium");
        case1.put("assignedTo", "Jane Doe");

        Map<String, Object> entityData1 = new HashMap<>();
        entityData1.put("entityName", "TechStart Innovations Pte Ltd");
        entityData1.put("entityType", "Non-Listed Company");
        entityData1.put("taxId", "202412345A");
        entityData1.put("address", "71 Ayer Rajah Crescent, #02-18, Singapore 139951");
        case1.put("entityData", entityData1);

        case1.put("relatedPartyLinks", new ArrayList<>());
        case1.put("accounts", new ArrayList<>());
        case1.put("activities", new ArrayList<>());
        case1.put("documentLinks", new ArrayList<>());

        cases.add(case1);

        // Mock case 2
        Map<String, Object> case2 = new HashMap<>();
        case2.put("caseId", "CASE-2025-002");
        case2.put("status", "Pending Checker Review");
        case2.put("riskLevel", "Low");
        case2.put("assignedTo", "John Smith");

        Map<String, Object> entityData2 = new HashMap<>();
        entityData2.put("entityName", "Lim & Tan Legal Associates");
        entityData2.put("entityType", "Partnership");
        entityData2.put("taxId", "T12PF3456G");
        entityData2.put("address", "1 Raffles Place, #44-01, Singapore 048616");
        case2.put("entityData", entityData2);

        case2.put("relatedPartyLinks", new ArrayList<>());
        case2.put("accounts", new ArrayList<>());
        case2.put("activities", new ArrayList<>());
        case2.put("documentLinks", new ArrayList<>());

        cases.add(case2);

        return cases;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCaseById(@PathVariable String id) {
        // Return a mock case
        Map<String, Object> mockCase = new HashMap<>();
        mockCase.put("caseId", id);
        mockCase.put("status", "Draft");
        mockCase.put("riskLevel", "Medium");
        mockCase.put("assignedTo", "Jane Doe");

        Map<String, Object> entityData = new HashMap<>();
        entityData.put("entityName", "TechStart Innovations Pte Ltd");
        entityData.put("entityType", "Non-Listed Company");
        entityData.put("taxId", "202412345A");
        entityData.put("address", "71 Ayer Rajah Crescent, #02-18, Singapore 139951");
        mockCase.put("entityData", entityData);

        // Add empty arrays for required fields
        mockCase.put("relatedPartyLinks", new ArrayList<>());
        mockCase.put("accounts", new ArrayList<>());
        mockCase.put("activities", Arrays.asList(
                Map.of("id", "A1", "timestamp", new Date().toString(), "actor", "System", "action", "Case Created")
        ));
        mockCase.put("documentLinks", new ArrayList<>());

        return mockCase;
    }

    @PostMapping
    public Map<String, Object> createCase(@RequestBody Map<String, Object> caseData) {
        // Return the same data with a generated ID
        caseData.put("caseId", "CASE-" + System.currentTimeMillis());
        return caseData;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateCase(@PathVariable String id, @RequestBody Map<String, Object> caseData) {
        caseData.put("caseId", id);
        return caseData;
    }
}