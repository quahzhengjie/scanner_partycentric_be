package com.bkb.scanner.repository;

import com.bkb.scanner.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findBySubmissionId(String submissionId);
    List<Submission> findByStatus(Submission.DocumentStatus status);
    boolean existsBySubmissionId(String submissionId);
}