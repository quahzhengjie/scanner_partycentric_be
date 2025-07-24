package com.bkb.scanner.controller;

import com.bkb.scanner.entity.*;
import com.bkb.scanner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

    private final UserRepository userRepository;
    private final CaseRepository caseRepository;
    private final PartyRepository partyRepository;
    private final DocumentRepository documentRepository;

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend is running!");
        return response;
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("users", userRepository.count());
        stats.put("cases", caseRepository.count());
        stats.put("parties", partyRepository.count());
        stats.put("documents", documentRepository.count());
        return stats;
    }

    @GetMapping("/test-data")
    public Map<String, Object> getTestData() {
        Map<String, Object> data = new HashMap<>();
        data.put("users", userRepository.findAll());
        data.put("cases", caseRepository.findAll());
        data.put("parties", partyRepository.findAll());
        return data;
    }
}