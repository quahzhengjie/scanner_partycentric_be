package com.bkb.scanner.repository;

import com.bkb.scanner.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    Optional<Party> findByPartyId(String partyId);
    List<Party> findByIsPEP(boolean isPEP);
    List<Party> findByType(Party.PartyType type);
    boolean existsByPartyId(String partyId);
}