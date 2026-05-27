package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.request.BuildingRequestDto;
import com.unistay.housing_management_system.dtos.response.BuildingResponseDto;
import com.unistay.housing_management_system.enums.BuildingGenderType;
import com.unistay.housing_management_system.services.BuildingService;
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
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private static final Logger logger = LoggerFactory.getLogger(BuildingController.class);

    private final BuildingService buildingService;

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingResponseDto> createBuilding(
            @Valid @RequestBody BuildingRequestDto dto) {
        logger.info("POST /api/buildings — name: {}", dto.getBuildingName());
        return ResponseEntity.status(HttpStatus.CREATED).body(buildingService.createBuilding(dto));
    }

    @GetMapping
    public ResponseEntity<List<BuildingResponseDto>> getAllBuildings(
            @RequestParam(required = false) BuildingGenderType genderType) {
        logger.info("GET /api/buildings — genderType filter: {}", genderType);

        List<BuildingResponseDto> result = (genderType != null)
                ? buildingService.getBuildingsByGenderType(genderType)
                : buildingService.getAllBuildings();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingResponseDto> getBuildingById(@PathVariable Long id) {
        logger.info("GET /api/buildings/{}", id);
        return ResponseEntity.ok(buildingService.getBuildingDtoById(id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingResponseDto> updateBuilding(
            @PathVariable Long id,
            @Valid @RequestBody BuildingRequestDto dto) {
        logger.info("PUT /api/buildings/{}", id);
        return ResponseEntity.ok(buildingService.updateBuilding(id, dto));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        logger.info("DELETE /api/buildings/{}", id);
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
