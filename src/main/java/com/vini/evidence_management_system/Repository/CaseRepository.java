package com.vini.evidence_management_system.Repository;

import com.vini.evidence_management_system.Entity.Case;
import com.vini.evidence_management_system.Entity.CaseStatus;
import com.vini.evidence_management_system.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    Optional<Case> findByCaseNumber(String caseNumber);
    boolean existsByCaseNumber(String caseNumber);
    List<Case> findByCreatedBy(User createdBy);
    List<Case> findByStatus(CaseStatus status);
}