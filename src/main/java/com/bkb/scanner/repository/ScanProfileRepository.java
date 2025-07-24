package com.bkb.scanner.repository;

import com.bkb.scanner.entity.ScanProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScanProfileRepository extends JpaRepository<ScanProfile, Long> {
    Optional<ScanProfile> findByProfileName(String profileName);
    List<ScanProfile> findByIsActiveTrue();
    boolean existsByProfileName(String profileName);
}