package com.vini.evidence_management_system.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseAssignmentResponse {

    private Long id;
    private Long caseId;
    private String caseNumber;
    private Long userId;
    private String username;
    private String assignedByUsername;
    private String assignedRole;
    private LocalDateTime assignedAt;
    private Boolean isActive;
}
