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
public class EvidenceResponse {

    private Long id;
    private String evidenceNumber;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String sha256Hash;
    private String description;
    private Boolean isVerified;
    private String uploadedBy;
    private Long caseId;
    private String caseNumber;
    private LocalDateTime uploadedAt;
}