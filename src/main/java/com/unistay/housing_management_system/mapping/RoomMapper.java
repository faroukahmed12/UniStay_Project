package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.Repository.BuildingRepository;
import com.unistay.housing_management_system.dtos.request.RoomRequestDto;
import com.unistay.housing_management_system.dtos.response.RoomResponseDto;
import com.unistay.housing_management_system.entity.Building;
import com.unistay.housing_management_system.entity.Room;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RoomMapper {

    @Autowired
    private BuildingRepository buildingRepository;

    @Mapping(source = "building.buildingId", target = "buildingId")
    @Mapping(source = "building.buildingName", target = "buildingName")
    public abstract RoomResponseDto toDto(Room room);

    public abstract List<RoomResponseDto> toDtoList(List<Room> rooms);

    // DTO → Entity
    @Mapping(target = "roomId", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "roomAssignments", ignore = true)
    @Mapping(target = "requestedRoomChanges", ignore = true)
    @Mapping(target = "currentRoomChanges", ignore = true)
    @Mapping(target = "maintenanceRequests", ignore = true)
    public abstract Room toEntity(RoomRequestDto dto);

    @AfterMapping
    protected void setBuilding(RoomRequestDto dto, @MappingTarget Room room) {
        Building building = buildingRepository.findByBuildingName(dto.getBuildingName())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with name: " + dto.getBuildingName()));
        room.setBuilding(building);
    }

    /*
    // Update existing entity (for PUT requests)
    @Mapping(target = "roomId", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "roomAssignments", ignore = true)
    @Mapping(target = "requestedRoomChanges", ignore = true)
    @Mapping(target = "currentRoomChanges", ignore = true)
    @Mapping(target = "maintenanceRequests", ignore = true)
    void updateEntityFromDto(RoomRequestDto dto, @MappingTarget Room room);

     */
}
