package com.unistay.housing_management_system.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RoomAssignmentCreateDto {
    @NotNull(message = "Student University ID is required")
    private String universityId;

    @NotNull(message = "Room Number is required")
    private String roomNumber;

    @NotNull(message = "Move-in date is required")
    private LocalDate moveInDate;

    @NotNull(message = "Assigned by (Admin ID) is required")
    private Long assignedById;
}
