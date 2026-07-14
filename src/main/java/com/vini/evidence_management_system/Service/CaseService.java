package com.vini.evidence_management_system.Service;

import com.vini.evidence_management_system.Dto.CaseRequest;
import com.vini.evidence_management_system.Dto.CaseResponse;
import com.vini.evidence_management_system.Entity.Case;
import com.vini.evidence_management_system.Entity.User;
import com.vini.evidence_management_system.Repository.CaseRepository;
import com.vini.evidence_management_system.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;

    public CaseResponse createCase(CaseRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String caseNumber = "CASE-" + System.currentTimeMillis();

        Case newCase = Case.builder()
                .caseNumber(caseNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .createdBy(user)
                .build();

        caseRepository.save(newCase);

        return mapToResponse(newCase);
    }

    public List<CaseResponse> getAllCases() {
        return caseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CaseResponse getCaseById(Long id) {
        Case c = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return mapToResponse(c);
    }

    public CaseResponse updateCase(Long id, CaseRequest request, String username) {
        Case c = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        c.setTitle(request.getTitle());
        c.setDescription(request.getDescription());
        c.setStatus(request.getStatus());
        caseRepository.save(c);

        return mapToResponse(c);
    }

    private CaseResponse mapToResponse(Case c) {
        return CaseResponse.builder()
                .id(c.getId())
                .caseNumber(c.getCaseNumber())
                .title(c.getTitle())
                .description(c.getDescription())
                .status(c.getStatus().name())
                .createdBy(c.getCreatedBy().getUsername())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}