package com.unistay.housing_management_system.dtos.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminDto extends UserDto{
    private String department;
}
