package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.request.RoomAssignmentCreateDto;
import com.unistay.housing_management_system.dtos.response.RoomAssignmentResponseDto;
import com.unistay.housing_management_system.services.RoomAssignmentService;
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
@RequestMapping("/api/room-assignments")
@RequiredArgsConstructor
public class RoomAssignmentController {

    private static final Logger logger = LoggerFactory.getLogger(RoomAssignmentController.class);

    private final RoomAssignmentService roomAssignmentService;

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomAssignmentResponseDto> assignRoom(
            @Valid @RequestBody RoomAssignmentCreateDto dto) {
        logger.info("POST /api/room-assignments — universityId: {}, roomNumber: {}",
                dto.getUniversityId(), dto.getRoomNumber());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomAssignmentService.assignRoom(dto));
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomAssignmentResponseDto>> getAllAssignments() {
        logger.info("GET /api/room-assignments");
        return ResponseEntity.ok(roomAssignmentService.getAllAssignments());
    }

    @GetMapping("/student/{studentId}/active")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<RoomAssignmentResponseDto> getActiveAssignment(
            @PathVariable Long studentId) {
        logger.info("GET /api/room-assignments/student/{}/active", studentId);
        return ResponseEntity.ok(roomAssignmentService.getActiveAssignmentByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/history")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<List<RoomAssignmentResponseDto>> getAssignmentHistory(
            @PathVariable Long studentId) {
        logger.info("GET /api/room-assignments/student/{}/history", studentId);
        return ResponseEntity.ok(roomAssignmentService.getAssignmentHistoryByStudentId(studentId));
    }

    @PatchMapping("/{assignmentId}/move-out")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomAssignmentResponseDto> moveOutStudent(
            @PathVariable Long assignmentId) {
        logger.info("PATCH /api/room-assignments/{}/move-out", assignmentId);
        return ResponseEntity.ok(roomAssignmentService.moveOutStudent(assignmentId));
    }
}
