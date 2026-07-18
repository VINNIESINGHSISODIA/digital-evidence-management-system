package com.vini.evidence_management_system.Controller;

import com.vini.evidence_management_system.Dto.CaseRequest;
import com.vini.evidence_management_system.Dto.CaseResponse;
import com.vini.evidence_management_system.Service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @PostMapping
    public ResponseEntity<CaseResponse> createCase(@Valid @RequestBody CaseRequest request,
                                                   Authentication authentication) {
        System.out.println("USERNAME FROM TOKEN: " + authentication.getName());
        return ResponseEntity.ok(caseService.createCase(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<CaseResponse>> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseResponse> updateCase(@PathVariable Long id,
                                                   @Valid @RequestBody CaseRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.ok(caseService.updateCase(id, request, authentication.getName()));
    }
}