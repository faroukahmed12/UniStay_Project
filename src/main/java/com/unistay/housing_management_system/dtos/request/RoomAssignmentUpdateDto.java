package com.unistay.housing_management_system.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RoomAssignmentUpdateDto {
    @NotNull(message = "Move-in date is required")
    private LocalDate moveOutDate;

    // Optional building info to validate/update room's building
    private Long buildingId;
    private String buildingName;

    @NotNull(message = "Room Number is required")
    private String roomNumber;
}
