package com.unistay.housing_management_system.dtos.response;

import com.unistay.housing_management_system.enums.RoomStatus;
import lombok.Data;

@Data
public class RoomResponseDto {
    private Long roomId;
    private String roomNumber;
    private Integer capacity;
    private Integer occupiedBeds;
    private RoomStatus status;
    private Long buildingId;
    private String buildingName;
}
