package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.dtos.response.UserDto;
import com.unistay.housing_management_system.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}