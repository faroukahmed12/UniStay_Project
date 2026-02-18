package com.unistay.housing_management_system.dtos.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RoomAssignmentResponseDto {
    private Long assignmentId;
    private LocalDate assignmentDate;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private RoomResponseDto roomDto;
    private StudentDto studentDto;
    private AdminDto assignedBy;
}
