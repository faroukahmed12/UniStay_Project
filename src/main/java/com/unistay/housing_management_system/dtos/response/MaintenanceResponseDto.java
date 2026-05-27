package com.unistay.housing_management_system.dtos.response;

import com.unistay.housing_management_system.enums.MaintenanceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaintenanceResponseDto {
    private Long id;
    private String issueType;
    private String description;
    private MaintenanceStatus status;
    private LocalDateTime submissionDate;
    private LocalDateTime resolvedDate;
    private String notes;

    private StudentDto student;
    private MaintenanceStaffDto assignedStaff;
    private BuildingResponseDto building;
    private RoomResponseDto room;
}
