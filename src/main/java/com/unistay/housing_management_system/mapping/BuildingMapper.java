package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.dtos.request.BuildingRequestDto;
import com.unistay.housing_management_system.dtos.response.BuildingResponseDto;
import com.unistay.housing_management_system.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoomMapper.class})
public interface BuildingMapper {

    BuildingResponseDto toDto(Building building);

    List<BuildingResponseDto> toDtoList(List<Building> buildings);


    @Mapping(target = "buildingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "maintenanceRequests", ignore = true)
    Building toEntity(BuildingRequestDto dto);
/*
    @Mapping(target = "buildingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "maintenanceRequests", ignore = true)
    void updateEntityFromDto(BuildingRequestDto dto, @MappingTarget Building building);
*/
}
