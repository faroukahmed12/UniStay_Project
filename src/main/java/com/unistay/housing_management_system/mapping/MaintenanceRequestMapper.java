package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.Repository.BuildingRepository;
import com.unistay.housing_management_system.Repository.MaintenanceStaffRepository;
import com.unistay.housing_management_system.Repository.RoomRepository;
import com.unistay.housing_management_system.Repository.StudentRepository;
import com.unistay.housing_management_system.dtos.request.MaintenanceRequestCreateDto;
import com.unistay.housing_management_system.dtos.request.MaintenanceRequestUpdateDto;
import com.unistay.housing_management_system.dtos.response.MaintenanceResponseDto;
import com.unistay.housing_management_system.entity.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StudentMapper.class, MaintenanceStaffMapper.class,
                                            BuildingMapper.class, RoomMapper.class})
public abstract class MaintenanceRequestMapper {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MaintenanceStaffRepository maintenanceStaffRepository;


    public abstract MaintenanceResponseDto toDto(MaintenanceRequest maintenanceRequest);
    public abstract List<MaintenanceResponseDto> toDtoList(List<MaintenanceRequest> maintenanceRequests);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "resolvedDate", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "assignedStaff", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "room", ignore = true)
    public abstract MaintenanceRequest toEntity(MaintenanceRequestCreateDto dto);

    @AfterMapping
    protected void setCreateRelations(MaintenanceRequestCreateDto dto,
                                      @MappingTarget MaintenanceRequest entity) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Building building = buildingRepository.findByBuildingName(dto.getBuildingName())
                .orElseThrow(() -> new RuntimeException("Building not found"));

        Room room = roomRepository.findByRoomNumber(dto.getRoomNumber())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        entity.setStudent(student);
        entity.setBuilding(building);
        entity.setRoom(room);
    }

    @Mapping(target = "issueType", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "assignedStaff", ignore = true)
    public abstract void updateEntityFromDto(MaintenanceRequestUpdateDto dto,
                                             @MappingTarget MaintenanceRequest entity);

    @AfterMapping
    protected void setUpdateRelations(MaintenanceRequestUpdateDto dto,
                                      @MappingTarget MaintenanceRequest entity) {
        if (dto.getAssignedStaffId() != null) {
            MaintenanceStaff staff = maintenanceStaffRepository.findById(dto.getAssignedStaffId())
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            entity.setAssignedStaff(staff);
        }
    }
}
