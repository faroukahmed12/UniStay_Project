package com.unistay.housing_management_system.dtos.response;

import com.unistay.housing_management_system.enums.MaintenanceStatus;

import java.time.LocalDateTime;

public class MaintenanceResponseDto {
    private Long id;
    private String issueType;
    private String description;
    private MaintenanceStatus status;
    private LocalDateTime submissionDate;
    private LocalDateTime resolvedDate;
    private String notes;



    private StudentDto studentDto;
    private MaintenanceStaffDto assignedStaffId;
    private BuildingResponseDto buildingResponseDto;
    private RoomResponseDto roomResponseDto;
}
