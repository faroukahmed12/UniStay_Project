package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.BuildingRepository;
import com.unistay.housing_management_system.dtos.request.BuildingRequestDto;
import com.unistay.housing_management_system.dtos.response.BuildingResponseDto;
import com.unistay.housing_management_system.entity.Building;
import com.unistay.housing_management_system.enums.BuildingGenderType;
import com.unistay.housing_management_system.exceptions.ResourceAlreadyExistsException;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import com.unistay.housing_management_system.mapping.BuildingMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private static final Logger logger = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    @Transactional
    public BuildingResponseDto createBuilding(BuildingRequestDto dto) {
        logger.info("Attempting to create building with name: {}", dto.getBuildingName());

        if (buildingRepository.existsByBuildingName(dto.getBuildingName())) {
            logger.warn("Building already exists with name: {}", dto.getBuildingName());
            throw new ResourceAlreadyExistsException(
                    "Building already exists with name: " + dto.getBuildingName());
        }

        Building building = buildingMapper.toEntity(dto);
        Building saved = buildingRepository.save(building);

        logger.info("Building created successfully with id: {}", saved.getBuildingId());
        return buildingMapper.toDto(saved);
    }

    public List<BuildingResponseDto> getAllBuildings() {
        logger.info("Fetching all buildings");

        List<Building> buildings = buildingRepository.findAll();
        logger.info("Total buildings found: {}", buildings.size());

        return buildingMapper.toDtoList(buildings);
    }

    public BuildingResponseDto getBuildingDtoById(Long id) {
        logger.info("Fetching building with id: {}", id);
        return buildingMapper.toDto(getBuildingById(id));
    }

    public List<BuildingResponseDto> getBuildingsByGenderType(BuildingGenderType genderType) {
        logger.info("Fetching buildings with genderType: {}", genderType);

        List<Building> buildings = buildingRepository.findByGenderType(genderType);
        logger.info("Found {} buildings with genderType: {}", buildings.size(), genderType);

        return buildings.stream()
                .map(buildingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BuildingResponseDto updateBuilding(Long id, BuildingRequestDto dto) {
        logger.info("Updating building with id: {}", id);

        Building building = getBuildingById(id);

        building.setBuildingName(dto.getBuildingName());
        building.setLocation(dto.getLocation());
        building.setTotalRooms(dto.getTotalRooms());
        building.setTotalCapacity(dto.getTotalCapacity());
        building.setGenderType(dto.getGenderType());

        Building updated = buildingRepository.save(building);
        logger.info("Building updated successfully with id: {}", id);

        return buildingMapper.toDto(updated);
    }

    @Transactional
    public void deleteBuilding(Long id) {
        logger.info("Deleting building with id: {}", id);

        Building building = getBuildingById(id);

        // Guard: cannot delete a building that has rooms with occupants
        boolean hasOccupiedRooms = building.getRooms().stream()
                .anyMatch(room -> room.getOccupiedBeds() != null && room.getOccupiedBeds() > 0);

        if (hasOccupiedRooms) {
            logger.warn("Cannot delete building [{}] — it has occupied rooms", id);
            throw new IllegalStateException(
                    "Cannot delete building with occupied rooms. Please reassign students first.");
        }

        buildingRepository.delete(building);
        logger.info("Building [{}] deleted successfully", id);
    }

    // helper methods
    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Building not found with id: {}", id);
                    return new ResourceNotFoundException("Building not found with id: " + id);
                });
    }
}