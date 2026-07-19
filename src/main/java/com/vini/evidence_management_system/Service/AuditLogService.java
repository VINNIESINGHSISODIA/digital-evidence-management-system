package com.vini.evidence_management_system.Service;

import com.vini.evidence_management_system.Dto.AuditLogResponse;
import com.vini.evidence_management_system.Entity.AuditLog;
import com.vini.evidence_management_system.Entity.Case;
import com.vini.evidence_management_system.Entity.Evidence;
import com.vini.evidence_management_system.Entity.User;
import com.vini.evidence_management_system.Repository.AuditLogRepository;
import com.vini.evidence_management_system.Repository.CaseRepository;
import com.vini.evidence_management_system.Repository.EvidenceRepository;
import com.vini.evidence_management_system.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final CaseRepository caseRepository;
    private final EvidenceRepository evidenceRepository;

    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAll()
                .stream().map(this::mapToResponse).toList();
    }

    public List<AuditLogResponse> getLogsByCase(Long caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return auditLogRepository.findByCaseEntity(caseEntity)
                .stream().map(this::mapToResponse).toList();
    }

    public List<AuditLogResponse> getLogsByEvidence(Long evidenceId) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        return auditLogRepository.findByEvidence(evidence)
                .stream().map(this::mapToResponse).toList();
    }

    public List<AuditLogResponse> getLogsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return auditLogRepository.findByUser(user)
                .stream().map(this::mapToResponse).toList();
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .username(log.getUser().getUsername())
                .action(log.getAction())
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .caseId(log.getCaseEntity() != null ? log.getCaseEntity().getId() : null)
                .caseNumber(log.getCaseEntity() != null ? log.getCaseEntity().getCaseNumber() : null)
                .evidenceId(log.getEvidence() != null ? log.getEvidence().getId() : null)
                .evidenceNumber(log.getEvidence() != null ? log.getEvidence().getEvidenceNumber() : null)
                .createdAt(log.getCreatedAt())
                .build();
    }
}