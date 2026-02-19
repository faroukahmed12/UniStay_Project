package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.dtos.response.StudentDto;
import com.unistay.housing_management_system.entity.Student;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface StudentMapper {
    StudentDto toDto(Student student);
    List<StudentDto> toDtoList(List<Student> students);
}