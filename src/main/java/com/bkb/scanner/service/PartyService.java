package com.bkb.scanner.service;

import com.bkb.scanner.entity.Party;
import com.bkb.scanner.entity.Document;
import com.bkb.scanner.repository.PartyRepository;
import com.bkb.scanner.repository.DocumentRepository;
import com.bkb.scanner.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PartyService {

    private final PartyRepository partyRepository;
    private final DocumentRepository documentRepository;

    public List<Party> getAllParties() {
        return partyRepository.findAll();
    }

    public Party getPartyByPartyId(String partyId) {
        return partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new ResourceNotFoundException("Party not found: " + partyId));
    }

    public Party createParty(Party party) {
        party.setPartyId("P" + System.currentTimeMillis());
        return partyRepository.save(party);
    }

    public Party updateParty(String partyId, Party partyUpdate) {
        Party existingParty = getPartyByPartyId(partyId);

        if (partyUpdate.getName() != null) {
            existingParty.setName(partyUpdate.getName());
        }
        if (partyUpdate.getEmail() != null) {
            existingParty.setEmail(partyUpdate.getEmail());
        }
        if (partyUpdate.getPhone() != null) {
            existingParty.setPhone(partyUpdate.getPhone());
        }
        if (partyUpdate.getAddress() != null) {
            existingParty.setAddress(partyUpdate.getAddress());
        }

        return partyRepository.save(existingParty);
    }

    public List<Document> getPartyDocuments(String partyId) {
        return documentRepository.findByOwnerPartyId(partyId);
    }
}