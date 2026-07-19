package com.vini.evidence_management_system.Controller;

import com.vini.evidence_management_system.Dto.AuditLogResponse;
import com.vini.evidence_management_system.Entity.AuditLog;
import com.vini.evidence_management_system.Service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(auditLogService.getLogsByCase(caseId));
    }

    @GetMapping("/evidence/{evidenceId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByEvidence(@PathVariable Long evidenceId) {
        return ResponseEntity.ok(auditLogService.getLogsByEvidence(evidenceId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(userId));
    }
}
