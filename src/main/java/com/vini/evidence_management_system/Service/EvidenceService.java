package com.vini.evidence_management_system.Service;

import com.vini.evidence_management_system.Dto.EvidenceResponse;
import com.vini.evidence_management_system.Entity.AuditLog;
import com.vini.evidence_management_system.Entity.Case;
import com.vini.evidence_management_system.Entity.Evidence;
import com.vini.evidence_management_system.Entity.User;
import com.vini.evidence_management_system.Repository.AuditLogRepository;
import com.vini.evidence_management_system.Repository.CaseRepository;
import com.vini.evidence_management_system.Repository.EvidenceRepository;
import com.vini.evidence_management_system.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Value("${file.upload.dir}")
    private String uploadDir;

    public EvidenceResponse uploadEvidence(Long caseId, String description,
                                           MultipartFile file, String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        // Generate SHA-256 hash
        String sha256Hash = generateSHA256(file.getBytes());

        // Save file to disk
        String evidenceNumber = "EVD-" + System.currentTimeMillis();
        String fileName = evidenceNumber + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        Evidence evidence = Evidence.builder()
                .caseEntity(caseEntity)
                .uploadedBy(user)
                .evidenceNumber(evidenceNumber)
                .fileName(file.getOriginalFilename())
                .filePath(filePath.toString())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .sha256Hash(sha256Hash)
                .description(description)
                .isVerified(false)
                .build();

        evidenceRepository.save(evidence);

        // Audit log
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .caseEntity(caseEntity)
                .evidence(evidence)
                .action("EVIDENCE_UPLOADED")
                .ipAddress(null)
                .details("File: " + file.getOriginalFilename())
                .build());

        return mapToResponse(evidence);
    }

    public List<EvidenceResponse> getEvidenceByCase(Long caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return evidenceRepository.findByCaseEntity(caseEntity)
                .stream().map(this::mapToResponse).toList();
    }

    public EvidenceResponse getEvidenceById(Long id) {
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        return mapToResponse(evidence);
    }

    public Evidence getEvidenceEntityById(Long id) {
        return evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
    }

    public byte[] viewEvidence(Long id, String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .caseEntity(evidence.getCaseEntity())
                .evidence(evidence)
                .action("EVIDENCE_VIEWED")
                .details("Viewed file: " + evidence.getFileName())
                .build());

        return Files.readAllBytes(Paths.get(evidence.getFilePath()));
    }

    public byte[] downloadEvidence(Long id, String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .caseEntity(evidence.getCaseEntity())
                .evidence(evidence)
                .action("EVIDENCE_DOWNLOADED")
                .details("Downloaded file: " + evidence.getFileName())
                .build());

        return Files.readAllBytes(Paths.get(evidence.getFilePath()));
    }

    public EvidenceResponse verifyEvidence(Long id) throws Exception {
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

        byte[] fileBytes = Files.readAllBytes(Paths.get(evidence.getFilePath()));
        String currentHash = generateSHA256(fileBytes);
        boolean isVerified = currentHash.equals(evidence.getSha256Hash());

        evidence.setIsVerified(isVerified);
        evidenceRepository.save(evidence);

        return mapToResponse(evidence);
    }

    public void deleteEvidence(Long id) {
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        evidenceRepository.delete(evidence);
    }

    private String generateSHA256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private EvidenceResponse mapToResponse(Evidence e) {
        return EvidenceResponse.builder()
                .id(e.getId())
                .evidenceNumber(e.getEvidenceNumber())
                .fileName(e.getFileName())
                .fileType(e.getFileType())
                .fileSize(e.getFileSize())
                .sha256Hash(e.getSha256Hash())
                .description(e.getDescription())
                .isVerified(e.getIsVerified())
                .uploadedBy(e.getUploadedBy().getUsername())
                .caseId(e.getCaseEntity().getId())
                .caseNumber(e.getCaseEntity().getCaseNumber())
                .uploadedAt(e.getUploadedAt())
                .build();
    }
}
