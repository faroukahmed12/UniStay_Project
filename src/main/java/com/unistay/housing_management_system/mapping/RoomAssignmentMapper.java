package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.Repository.AdminRepository;
import com.unistay.housing_management_system.Repository.RoomRepository;
import com.unistay.housing_management_system.Repository.StudentRepository;
import com.unistay.housing_management_system.dtos.request.RoomAssignmentCreateDto;
import com.unistay.housing_management_system.dtos.request.RoomAssignmentUpdateDto;
import com.unistay.housing_management_system.dtos.response.RoomAssignmentResponseDto;
import com.unistay.housing_management_system.entity.Admin;
import com.unistay.housing_management_system.entity.Room;
import com.unistay.housing_management_system.entity.RoomAssignment;
import com.unistay.housing_management_system.entity.Student;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        RoomMapper.class,
        StudentMapper.class,
        AdminMapper.class
})
public abstract class RoomAssignmentMapper {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AdminRepository adminRepository;

    public abstract RoomAssignmentResponseDto toDto(RoomAssignment entity);
    public abstract List<RoomAssignmentResponseDto> toDtoList(List<RoomAssignment> entities);


    @Mapping(target = "assignmentId", ignore = true)
    @Mapping(target = "assignmentDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "moveOutDate", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "assignedBy", ignore = true)
    public abstract RoomAssignment toEntity(RoomAssignmentCreateDto dto);

    @AfterMapping
    protected void setCreateRelations(RoomAssignmentCreateDto dto,
                                      @MappingTarget RoomAssignment entity) {
        Student student = studentRepository.findByUniversityId(dto.getUniversityId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Room room = roomRepository.findByRoomNumber(dto.getRoomNumber())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Admin admin = adminRepository.findById(dto.getAssignedById())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        entity.setStudent(student);
        entity.setRoom(room);
        entity.setAssignedBy(admin);
    }


    @Mapping(target = "assignmentId", ignore = true)
    @Mapping(target = "assignmentDate", ignore = true)
    @Mapping(target = "moveInDate", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "assignedBy", ignore = true)
    @Mapping(target = "room", ignore = true)
    public abstract void updateEntityFromDto(RoomAssignmentUpdateDto dto,
                                             @MappingTarget RoomAssignment entity);

    @AfterMapping
    protected void setUpdateRelations(RoomAssignmentUpdateDto dto,
                                      @MappingTarget RoomAssignment entity) {
        Room room = roomRepository.findByRoomNumber(dto.getRoomNumber())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        entity.setRoom(room);
    }
}