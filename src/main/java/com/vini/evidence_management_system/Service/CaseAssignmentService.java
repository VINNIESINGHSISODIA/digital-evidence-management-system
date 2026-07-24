package com.vini.evidence_management_system.Service;

import com.vini.evidence_management_system.Dto.CaseAssignmentResponse;
import com.vini.evidence_management_system.Entity.AssignedRole;
import com.vini.evidence_management_system.Entity.Case;
import com.vini.evidence_management_system.Entity.CaseAssignment;
import com.vini.evidence_management_system.Entity.User;
import com.vini.evidence_management_system.Repository.CaseAssignmentRepository;
import com.vini.evidence_management_system.Repository.CaseRepository;
import com.vini.evidence_management_system.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaseAssignmentService {

    private final CaseAssignmentRepository caseAssignmentRepository;
    private final CaseRepository caseRepository;
    private final UserRepository userRepository;

    public CaseAssignmentResponse assignUserToCase(Long caseId, Long userId, String assignedByUsername, AssignedRole assignedRole) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User assignedBy = userRepository.findByUsername(assignedByUsername)
                .orElseThrow(() -> new RuntimeException("Assigned by user not found"));

        if (caseAssignmentRepository.existsByCaseEntityAndUserAndIsActiveTrue(caseEntity, user)) {
            throw new RuntimeException("User already assigned to this case");
        }

        CaseAssignment assignment = CaseAssignment.builder()
                .caseEntity(caseEntity)
                .user(user)
                .assignedBy(assignedBy)
                .assignedRole(assignedRole)
                .isActive(true)
                .build();

        return mapToResponse(caseAssignmentRepository.save(assignment));
    }

    public List<CaseAssignmentResponse> getAssignmentsByCase(Long caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return caseAssignmentRepository.findByCaseEntityAndIsActiveTrue(caseEntity)
                .stream().map(this::mapToResponse).toList();
    }

    public CaseAssignmentResponse deactivateAssignment(Long id) {
        CaseAssignment assignment = caseAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setIsActive(false);
        return mapToResponse(caseAssignmentRepository.save(assignment));
    }

    private CaseAssignmentResponse mapToResponse(CaseAssignment a) {
        return CaseAssignmentResponse.builder()
                .id(a.getId())
                .caseId(a.getCaseEntity().getId())
                .caseNumber(a.getCaseEntity().getCaseNumber())
                .userId(a.getUser().getId())
                .username(a.getUser().getUsername())
                .assignedByUsername(a.getAssignedBy().getUsername())
                .assignedRole(a.getAssignedRole().name())
                .assignedAt(a.getAssignedAt())
                .isActive(a.getIsActive())
                .build();
    }
}