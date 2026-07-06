package com.vini.evidence_management_system.Repository;

import com.vini.evidence_management_system.Entity.Case;
import com.vini.evidence_management_system.Entity.Evidence;
import com.vini.evidence_management_system.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByCaseEntity(Case caseEntity);
    Optional<Evidence> findByEvidenceNumber(String evidenceNumber);
    boolean existsByEvidenceNumber(String evidenceNumber);
    List<Evidence> findByUploadedBy(User uploadedBy);
}