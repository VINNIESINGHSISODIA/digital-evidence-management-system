package com.vini.evidence_management_system.Controller;

import com.vini.evidence_management_system.Dto.CaseAssignmentResponse;
import com.vini.evidence_management_system.Entity.AssignedRole;
import com.vini.evidence_management_system.Service.CaseAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class CaseAssignmentController {

    private final CaseAssignmentService caseAssignmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CaseAssignmentResponse> assignUserToCase(
            @RequestParam Long caseId,
            @RequestParam Long userId,
            @RequestParam AssignedRole assignedRole,
            Authentication authentication) {
        return ResponseEntity.ok(caseAssignmentService.assignUserToCase(
                caseId, userId, authentication.getName(), assignedRole));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<CaseAssignmentResponse>> getAssignmentsByCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(caseAssignmentService.getAssignmentsByCase(caseId));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CaseAssignmentResponse> deactivateAssignment(@PathVariable Long id) {
        return ResponseEntity.ok(caseAssignmentService.deactivateAssignment(id));
    }
}
