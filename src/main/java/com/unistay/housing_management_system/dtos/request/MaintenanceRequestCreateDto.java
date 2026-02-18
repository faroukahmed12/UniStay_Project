package com.unistay.housing_management_system.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaintenanceRequestCreateDto {
    @NotBlank(message = "Issue type is required")
    @Size(max = 50)
    private String issueType;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    /*@NotNull(message = "Building ID is required")
    private Long buildingId;*/
    @NotNull(message = "Building ID is required")
    private String buildingName;

    /*
    @NotNull(message = "Room ID is required")
    private Long roomId;*/
    @NotNull(message = "Room Number is required")
    private String roomNumber;

}
