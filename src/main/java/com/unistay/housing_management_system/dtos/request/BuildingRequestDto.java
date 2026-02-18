package com.unistay.housing_management_system.dtos.request;

import com.unistay.housing_management_system.enums.BuildingGenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BuildingRequestDto {

    @NotBlank(message = "Building name is required")
    @Size(max = 100)
    private String buildingName;

    @Size(max = 255)
    private String location;

    private Integer totalRooms;

    private Integer totalCapacity;

    @NotNull(message = "Gender type is required")
    private BuildingGenderType genderType;


}
