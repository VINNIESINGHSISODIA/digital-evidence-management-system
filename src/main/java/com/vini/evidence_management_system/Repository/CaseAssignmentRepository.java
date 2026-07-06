package com.vini.evidence_management_system.Repository;

import com.vini.evidence_management_system.Entity.Case;
import com.vini.evidence_management_system.Entity.CaseAssignment;
import com.vini.evidence_management_system.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, Long> {
    List<CaseAssignment> findByCaseEntity(Case caseEntity);
    List<CaseAssignment> findByUser(User user);
    boolean existsByCaseEntityAndUserAndIsActiveTrue(Case caseEntity, User user);
    List<CaseAssignment> findByCaseEntityAndIsActiveTrue(Case caseEntity);
}
