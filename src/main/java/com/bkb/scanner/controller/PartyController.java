package com.bkb.scanner.controller;

import com.bkb.scanner.entity.Party;
import com.bkb.scanner.entity.Document;
import com.bkb.scanner.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parties")
@RequiredArgsConstructor
// Option 2: If you must keep @CrossOrigin, use specific origins:

public class PartyController {

    private final PartyService partyService;

    @GetMapping
    public ResponseEntity<List<Party>> getAllParties() {
        return ResponseEntity.ok(partyService.getAllParties());
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<Party> getPartyById(@PathVariable String partyId) {
        return ResponseEntity.ok(partyService.getPartyByPartyId(partyId));
    }

    @PostMapping
    public ResponseEntity<Party> createParty(@RequestBody Party party) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partyService.createParty(party));
    }

    @PutMapping("/{partyId}")
    public ResponseEntity<Party> updateParty(
            @PathVariable String partyId,
            @RequestBody Party partyUpdate) {
        return ResponseEntity.ok(partyService.updateParty(partyId, partyUpdate));
    }

    @GetMapping("/{partyId}/documents")
    public ResponseEntity<List<Document>> getPartyDocuments(@PathVariable String partyId) {
        return ResponseEntity.ok(partyService.getPartyDocuments(partyId));
    }
}