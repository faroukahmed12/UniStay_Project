package com.unistay.housing_management_system.dtos.request;

import jakarta.validation.constraints.NotNull;

public class HousingApplicationRequestCreateDto {
    private String documentationPath;

    @NotNull(message = "University ID is required")
    private String universityId;
}
