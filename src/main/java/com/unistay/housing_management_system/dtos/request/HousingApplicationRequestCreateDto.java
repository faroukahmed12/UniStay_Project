package com.unistay.housing_management_system.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HousingApplicationRequestCreateDto {
    private String documentationPath;

    @NotNull(message = "University ID is required")
    private String universityId;
}
