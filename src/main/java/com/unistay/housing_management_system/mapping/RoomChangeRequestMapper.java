package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.Repository.AdminRepository;
import com.unistay.housing_management_system.Repository.RoomRepository;
import com.unistay.housing_management_system.Repository.StudentRepository;
import com.unistay.housing_management_system.dtos.request.RoomChangeRequestCreateDto;
import com.unistay.housing_management_system.dtos.request.RoomChangeRequestUpdateDto;
import com.unistay.housing_management_system.dtos.response.RoomChangeResponseDto;
import com.unistay.housing_management_system.entity.Admin;
import com.unistay.housing_management_system.entity.Room;
import com.unistay.housing_management_system.entity.RoomChangeRequest;
import com.unistay.housing_management_system.entity.Student;
import com.unistay.housing_management_system.exceptions.ResourceAlreadyExistsException;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        StudentMapper.class,
        AdminMapper.class,
        RoomMapper.class
})
public abstract class RoomChangeRequestMapper {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoomRepository roomRepository;


    public abstract RoomChangeResponseDto toDto(RoomChangeRequest entity);
    public abstract List<RoomChangeResponseDto> toDtoList(List<RoomChangeRequest> entities);


    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "requestDate", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "currentRoom", ignore = true)
    @Mapping(target = "requestedRoom", ignore = true)
    public abstract RoomChangeRequest toEntity(RoomChangeRequestCreateDto dto);

    @AfterMapping
    protected void setCreateRelations(RoomChangeRequestCreateDto dto,
                                      @MappingTarget RoomChangeRequest entity) {

        Student student = studentRepository.findByUniversityId(dto.getStudentUniversityId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with university ID: " + dto.getStudentUniversityId()));

        Room currentRoom = roomRepository.findByRoomNumber(dto.getCurrentRoomNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Current room not found with number: " + dto.getCurrentRoomNumber()));

        entity.setStudent(student);
        entity.setCurrentRoom(currentRoom);

        if (dto.getRequestedRoomNumber() != null) {
            Room requestedRoom = roomRepository.findByRoomNumber(dto.getRequestedRoomNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Requested room not found with number: " + dto.getRequestedRoomNumber()));
            entity.setRequestedRoom(requestedRoom);
        }
    }


    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "requestDate", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "currentRoom", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "requestedRoom", ignore = true)
    @Mapping(target = "reviewDate", expression = "java(java.time.LocalDate.now())")
    public abstract void updateEntityFromDto(RoomChangeRequestUpdateDto dto,
                                             @MappingTarget RoomChangeRequest entity);

    @AfterMapping
    protected void setUpdateRelations(RoomChangeRequestUpdateDto dto,
                                      @MappingTarget RoomChangeRequest entity) {

        Admin admin = adminRepository.findById(dto.getReviewedById())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + dto.getReviewedById()));
        entity.setReviewedBy(admin);

        if (dto.getRequestedRoomNumber() != null) {
            Room requestedRoom = roomRepository.findByRoomNumber(dto.getRequestedRoomNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Requested room not found with number: " + dto.getRequestedRoomNumber()));
            entity.setRequestedRoom(requestedRoom);
        }
    }
}