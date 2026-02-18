package com.unistay.housing_management_system.dtos.request;

import com.unistay.housing_management_system.enums.RoomChangeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomChangeRequestUpdateDto {
    @NotNull(message = "Status is required")
    private RoomChangeStatus status;

    @NotNull(message = "Reviewer By (Admin ID) is required")
    private Long reviewedById;

    private String requestedRoomNumber;
}
