package com.unistay.housing_management_system.dtos.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentDto extends UserDto{
    private String nationalId;
    private String universityId;
    private int secondaryCode;
    private String academicYear;
    private String faculty;
    private BigDecimal gpa;
    private String address;
}
