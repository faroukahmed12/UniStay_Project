package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.request.RoomChangeRequestCreateDto;
import com.unistay.housing_management_system.dtos.request.RoomChangeRequestUpdateDto;
import com.unistay.housing_management_system.dtos.response.RoomChangeResponseDto;
import com.unistay.housing_management_system.enums.RoomChangeStatus;
import com.unistay.housing_management_system.services.RoomChangeRequestService;
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
@RequestMapping("/api/room-change-requests")
@RequiredArgsConstructor
public class RoomChangeRequestController {

    private static final Logger logger = LoggerFactory.getLogger(RoomChangeRequestController.class);

    private final RoomChangeRequestService roomChangeRequestService;

    @PostMapping
    public ResponseEntity<RoomChangeResponseDto> createRequest(
            @Valid @RequestBody RoomChangeRequestCreateDto dto) {
        logger.info("POST /api/room-change-requests — studentUniversityId: {}, currentRoom: {}",
                dto.getStudentUniversityId(), dto.getCurrentRoomNumber());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomChangeRequestService.createRequest(dto));
    }

    @GetMapping
    public ResponseEntity<List<RoomChangeResponseDto>> getAllRequests(
            @RequestParam(required = false) RoomChangeStatus status) {
        logger.info("GET /api/room-change-requests — status filter: {}", status);

        List<RoomChangeResponseDto> result = (status != null)
                ? roomChangeRequestService.getRequestsByStatus(status)
                : roomChangeRequestService.getAllRequests();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomChangeResponseDto> getRequestById(@PathVariable Long id) {
        logger.info("GET /api/room-change-requests/{}", id);
        return ResponseEntity.ok(roomChangeRequestService.getRequestById(id));
    }

    @PatchMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomChangeResponseDto> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody RoomChangeRequestUpdateDto dto) {
        logger.info("PATCH /api/room-change-requests/{} — status: {}, overrideRoom: {}",
                id, dto.getStatus(), dto.getRequestedRoomNumber());
        return ResponseEntity.ok(roomChangeRequestService.updateRequest(id, dto));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        logger.info("DELETE /api/room-change-requests/{}", id);
        roomChangeRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
