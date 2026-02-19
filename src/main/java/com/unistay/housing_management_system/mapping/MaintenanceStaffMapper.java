package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.dtos.response.MaintenanceStaffDto;
import com.unistay.housing_management_system.entity.MaintenanceStaff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MaintenanceStaffMapper {
    MaintenanceStaffDto toDto(MaintenanceStaff staff);
    List<MaintenanceStaffDto> toDtoList(List<MaintenanceStaff> staffList);
}