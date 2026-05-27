package com.unistay.housing_management_system.dtos.response;

import com.unistay.housing_management_system.enums.GenderType;
import com.unistay.housing_management_system.enums.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private GenderType gender;
    private UserType userType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
