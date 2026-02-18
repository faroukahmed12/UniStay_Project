package com.unistay.housing_management_system.dtos.request;

import com.unistay.housing_management_system.enums.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoomRequestDto {
    @NotBlank(message = "Room number is required")
    @Size(max = 30)
    private String roomNumber;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Integer occupiedBeds = 0;

    private RoomStatus status = RoomStatus.AVAILABLE;

    /*
    @NotNull(message = "Building ID is required")
    private Long buildingId;
    */

    @NotBlank(message = "Building Name is required")
    @Size(max = 30)
    private String buildingName;
}
