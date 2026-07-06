package com.vini.evidence_management_system.Repository;

import com.vini.evidence_management_system.Entity.AuditLog;
import com.vini.evidence_management_system.Entity.Case;
import com.vini.evidence_management_system.Entity.Evidence;
import com.vini.evidence_management_system.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByCaseEntity(Case caseEntity);
    List<AuditLog> findByEvidence(Evidence evidence);
    List<AuditLog> findByEntityType(String entityType);
}