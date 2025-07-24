package com.bkb.scanner.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/mockparties")
public class MockPartyController {

    @GetMapping
    public List<Map<String, Object>> getAllParties() {
        List<Map<String, Object>> parties = new ArrayList<>();

        parties.add(createParty("P001", "John Tan", "Singaporean/PR", false));
        parties.add(createParty("P002", "Michael Lim", "Singaporean/PR", false));
        parties.add(createParty("P003", "Sarah Chen", "Foreigner", false));
        parties.add(createParty("P004", "David Lim", "Singaporean/PR", false));
        parties.add(createParty("P005", "Jessica Tan", "Singaporean/PR", false));
        parties.add(createParty("P006", "Robert Wang", "Foreigner", true));

        return parties;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getPartyById(@PathVariable String id) {
        // Return a mock party based on ID
        return createParty(id, "Mock Party " + id, "Singaporean/PR", false);
    }

    private Map<String, Object> createParty(String id, String name, String residencyStatus, boolean isPEP) {
        Map<String, Object> party = new HashMap<>();
        party.put("partyId", id);
        party.put("name", name);
        party.put("residencyStatus", residencyStatus);
        party.put("isPEP", isPEP);
        party.put("documentLinks", new ArrayList<>());
        return party;
    }

    @PostMapping
    public Map<String, Object> createParty(@RequestBody Map<String, Object> partyData) {
        partyData.put("partyId", "P" + System.currentTimeMillis());
        return partyData;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateParty(@PathVariable String id, @RequestBody Map<String, Object> partyData) {
        partyData.put("partyId", id);
        return partyData;
    }
}