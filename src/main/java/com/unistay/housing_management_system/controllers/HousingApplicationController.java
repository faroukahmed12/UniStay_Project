package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.request.HousingApplicationRequestCreateDto;
import com.unistay.housing_management_system.dtos.response.HousingApplicationResponseDto;
import com.unistay.housing_management_system.enums.HousingApplicationStatus;
import com.unistay.housing_management_system.services.HousingApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/housing-applications")
@RequiredArgsConstructor
public class HousingApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(HousingApplicationController.class);

    private final HousingApplicationService housingApplicationService;

    @PostMapping
//    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<HousingApplicationResponseDto> createHousingApplication(
            @Valid @RequestBody HousingApplicationRequestCreateDto dto) {
        logger.info("POST /api/housing-applications — universityId: {}", dto.getUniversityId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(housingApplicationService.createHousingApplication(dto));
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HousingApplicationResponseDto>> getAllApplications() {
        logger.info("GET /api/housing-applications");
        return ResponseEntity.ok(housingApplicationService.getAllHousingApplications());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HousingApplicationResponseDto> getApplicationById(
            @PathVariable Long id) {
        logger.info("GET /api/housing-applications/{}", id);
        return ResponseEntity.ok(housingApplicationService.getHousingApplicationById(id));
    }

    @GetMapping("/student/{universityId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<List<HousingApplicationResponseDto>> getApplicationsByStudent(
            @PathVariable String universityId) {
        logger.info("GET /api/housing-applications/student/{}", universityId);
        return ResponseEntity.ok(
                housingApplicationService.getHousingApplicationsByStudentUniversityId(universityId));
    }

    @PatchMapping("/{id}/status")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HousingApplicationResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam HousingApplicationStatus status) {
        logger.info("PATCH /api/housing-applications/{}/status — newStatus: {}", id, status);
        return ResponseEntity.ok(
                housingApplicationService.updateHousingApplicationStatus(id, status));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        logger.info("DELETE /api/housing-applications/{}", id);
        housingApplicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}
