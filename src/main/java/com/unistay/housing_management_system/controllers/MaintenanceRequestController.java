package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.request.MaintenanceRequestCreateDto;
import com.unistay.housing_management_system.dtos.request.MaintenanceRequestUpdateDto;
import com.unistay.housing_management_system.dtos.response.MaintenanceResponseDto;
import com.unistay.housing_management_system.enums.MaintenanceStatus;
import com.unistay.housing_management_system.services.AdminService;
import com.unistay.housing_management_system.services.AuthService;
import com.unistay.housing_management_system.services.MaintenanceRequestService;
import com.unistay.housing_management_system.services.StudentService;
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
@RequestMapping("/api/maintenance-requests")
@RequiredArgsConstructor
public class MaintenanceRequestController {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceRequestController.class);

    private final MaintenanceRequestService maintenanceRequestService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<MaintenanceResponseDto> createRequest(
            @Valid @RequestBody MaintenanceRequestCreateDto dto) {
        logger.info("POST /api/maintenance-requests — studentId: {}, issueType: {}",
                authService.getCurrentUserId(), dto.getIssueType());
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceRequestService.createRequest(dto));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceResponseDto>> getAllRequests(
            @RequestParam(required = false) MaintenanceStatus status) {
        logger.info("GET /api/maintenance-requests — status filter: {}", status);

        List<MaintenanceResponseDto> result = (status != null)
                ? maintenanceRequestService.getRequestsByStatus(status)
                : maintenanceRequestService.getAllRequests();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/count/pending")
    public ResponseEntity<Long> getPendingRequestsCount() {
        logger.info("GET /api/maintenance-requests/count/pending");
        return ResponseEntity.ok(maintenanceRequestService.getPendingMaintenanceRequestsCount());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDto> getRequestById(@PathVariable Long id) {
        logger.info("GET /api/maintenance-requests/{}", id);
        return ResponseEntity.ok(maintenanceRequestService.getRequestById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<MaintenanceResponseDto>> getRequestsByStudent(
            @PathVariable Long studentId) {
        logger.info("GET /api/maintenance-requests/student/{}", studentId);
        return ResponseEntity.ok(maintenanceRequestService.getRequestsByStudentId(studentId));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<MaintenanceResponseDto>> getRequestsByStaff(
            @PathVariable Long staffId) {
        logger.info("GET /api/maintenance-requests/staff/{}", staffId);
        return ResponseEntity.ok(maintenanceRequestService.getRequestsByStaffId(staffId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDto> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequestUpdateDto dto) {
        logger.info("PATCH /api/maintenance-requests/{} — status: {}, assignedStaffId: {}",
                id, dto.getStatus(), dto.getAssignedStaffId());
        return ResponseEntity.ok(maintenanceRequestService.updateRequest(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        logger.info("DELETE /api/maintenance-requests/{}", id);
        maintenanceRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
