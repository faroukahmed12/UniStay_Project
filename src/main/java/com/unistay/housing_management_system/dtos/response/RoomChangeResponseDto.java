package com.unistay.housing_management_system.dtos.response;

import com.unistay.housing_management_system.enums.RoomChangeStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RoomChangeResponseDto {
    private Long requestId;
    private String reason;
    private RoomChangeStatus status;
    private LocalDate requestDate;
    private LocalDate reviewDate;

    private StudentDto studentDto;
    private AdminDto reviewedBy;
    private RoomResponseDto currentRoomDto;
    private RoomResponseDto requestedRoomDto;
}
