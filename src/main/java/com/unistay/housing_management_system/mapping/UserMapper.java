package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.dtos.response.UserDto;
import com.unistay.housing_management_system.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "hashedPassword", ignore = true)
    UserDto toDto(User user);
}