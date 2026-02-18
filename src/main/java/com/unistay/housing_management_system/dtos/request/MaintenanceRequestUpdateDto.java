package com.unistay.housing_management_system.dtos.request;

import com.unistay.housing_management_system.enums.MaintenanceStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaintenanceRequestUpdateDto {

    private Long id;

    private MaintenanceStatus status;

    private Long assignedStaffId;

    private LocalDateTime resolvedDate;

    @Size(max = 1000)
    private String notes;
}
