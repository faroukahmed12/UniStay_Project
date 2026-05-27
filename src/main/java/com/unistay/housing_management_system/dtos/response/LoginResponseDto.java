package com.unistay.housing_management_system.dtos.response;

public record LoginResponseDto(
        String token,
        String userType,
        UserDto userDto
) {}
