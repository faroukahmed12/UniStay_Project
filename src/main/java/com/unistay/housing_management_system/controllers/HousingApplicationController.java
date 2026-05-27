package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.request.HousingApplicationRequestCreateDto;
import com.unistay.housing_management_system.dtos.response.HousingApplicationResponseDto;
import com.unistay.housing_management_system.enums.HousingApplicationStatus;
import com.unistay.housing_management_system.services.FileStorageService;
import com.unistay.housing_management_system.services.HousingApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/housing-applications")
@RequiredArgsConstructor
public class HousingApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(HousingApplicationController.class);

    private final HousingApplicationService housingApplicationService;
    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createApplication(
            @RequestParam("universityId") String universityId,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        String filePath = null;
        if (file != null && !file.isEmpty()) {
            filePath = fileStorageService.storeFile(file, universityId);
        }


        return ResponseEntity.status(201)
                .body(housingApplicationService.createApplication(universityId, filePath));
    }

    // تحميل الملف
    @GetMapping("/download/{applicationId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long applicationId) {
        HousingApplicationResponseDto app = housingApplicationService.getHousingApplicationById(applicationId);
        Resource resource = fileStorageService.loadFile(app.getDocumentationPath());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<List<HousingApplicationResponseDto>> getAllApplications() {
        logger.info("GET /api/housing-applications");
        return ResponseEntity.ok(housingApplicationService.getAllHousingApplications());
    }

    @GetMapping("/count/pending")
    public ResponseEntity<Long> getPendingApplicationsCount() {
        logger.info("GET /api/housing-applications/count/pending");
        return ResponseEntity.ok(housingApplicationService.getPendingHousingApplicationsCount());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HousingApplicationResponseDto> getApplicationById(
            @PathVariable Long id) {
        logger.info("GET /api/housing-applications/{}", id);
        return ResponseEntity.ok(housingApplicationService.getHousingApplicationById(id));
    }

    @GetMapping("/student/{universityId}")
    public ResponseEntity<List<HousingApplicationResponseDto>> getApplicationsByStudent(
            @PathVariable String universityId) {
        logger.info("GET /api/housing-applications/student/{}", universityId);
        return ResponseEntity.ok(
                housingApplicationService.getHousingApplicationsByStudentUniversityId(universityId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<HousingApplicationResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam HousingApplicationStatus status,
            @RequestParam(required = false) String rejectionReason) {
        logger.info("PATCH /api/housing-applications/{}/status — newStatus: {}", id, status);
        return ResponseEntity.ok(
                housingApplicationService.updateHousingApplicationStatus(id, status, rejectionReason));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        logger.info("DELETE /api/housing-applications/{}", id);
        housingApplicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}
