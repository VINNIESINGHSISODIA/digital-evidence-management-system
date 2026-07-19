package com.vini.evidence_management_system.Controller;

import com.vini.evidence_management_system.Dto.EvidenceResponse;
import com.vini.evidence_management_system.Entity.Evidence;
import com.vini.evidence_management_system.Service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/evidence")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping("/upload")
    public ResponseEntity<EvidenceResponse> uploadEvidence(
            @RequestParam("caseId") Long caseId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws Exception {
        String username = authentication.getName();
        return ResponseEntity.ok(evidenceService.uploadEvidence(caseId, description, file, username));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<EvidenceResponse>> getEvidenceByCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(evidenceService.getEvidenceByCase(caseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvidenceResponse> getEvidenceById(@PathVariable Long id) {
        return ResponseEntity.ok(evidenceService.getEvidenceById(id));
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<byte[]> viewEvidence(@PathVariable Long id,
                                               Authentication authentication) throws Exception {
        String username = authentication.getName();
        byte[] fileBytes = evidenceService.viewEvidence(id, username);
        Evidence evidence = evidenceService.getEvidenceEntityById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, evidence.getFileType())
                .body(fileBytes);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadEvidence(@PathVariable Long id,
                                                   Authentication authentication) throws Exception {
        String username = authentication.getName();
        byte[] fileBytes = evidenceService.downloadEvidence(id, username);
        Evidence evidence = evidenceService.getEvidenceEntityById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + evidence.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, evidence.getFileType())
                .body(fileBytes);
    }

    @GetMapping("/{id}/verify")
    public ResponseEntity<EvidenceResponse> verifyEvidence(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(evidenceService.verifyEvidence(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvidence(@PathVariable Long id) {
        evidenceService.deleteEvidence(id);
        return ResponseEntity.noContent().build();
    }
}
