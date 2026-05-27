package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.request.RoomRequestDto;
import com.unistay.housing_management_system.dtos.response.RoomResponseDto;
import com.unistay.housing_management_system.enums.RoomStatus;
import com.unistay.housing_management_system.services.RoomService;
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
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(
            @Valid @RequestBody RoomRequestDto dto) {
        logger.info("POST /api/rooms — roomNumber: {}, buildingId: {}",
                dto.getRoomNumber(), dto.getBuildingId());
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(dto));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms(
            @RequestParam(required = false) RoomStatus status) {
        logger.info("GET /api/rooms — status filter: {}", status);

        List<RoomResponseDto> result = (status != null)
                ? roomService.getRoomsByStatus(status)
                : roomService.getAllRooms();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long id) {
        logger.info("GET /api/rooms/{}", id);
        return ResponseEntity.ok(roomService.getRoomDtoById(id));
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<RoomResponseDto>> getRoomsByBuilding(
            @PathVariable Long buildingId) {
        logger.info("GET /api/rooms/building/{}", buildingId);
        return ResponseEntity.ok(roomService.getRoomsByBuilding(buildingId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponseDto>> getAvailableRoomsByBuildingName(
            @RequestParam String buildingName) {
        logger.info("GET /api/rooms/available?buildingName={}", buildingName);
        return ResponseEntity.ok(roomService.getAvailableRoomsByBuildingName(buildingName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDto dto) {
        logger.info("PUT /api/rooms/{}", id);
        return ResponseEntity.ok(roomService.updateRoom(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        logger.info("DELETE /api/rooms/{}", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
