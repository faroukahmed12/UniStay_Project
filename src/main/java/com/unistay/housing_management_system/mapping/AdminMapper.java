package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.dtos.response.AdminDto;
import com.unistay.housing_management_system.entity.Admin;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AdminMapper {
    AdminDto toDto(Admin admin);
    List<AdminDto> toDtoList(List<Admin> admins);
}