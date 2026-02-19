package com.unistay.housing_management_system.mapping;

import com.unistay.housing_management_system.Repository.AdminRepository;
import com.unistay.housing_management_system.Repository.StudentRepository;
import com.unistay.housing_management_system.dtos.request.HousingApplicationRequestCreateDto;
import com.unistay.housing_management_system.dtos.request.HousingApplicationRequestReviewDto;
import com.unistay.housing_management_system.dtos.response.HousingApplicationResponseDto;
import com.unistay.housing_management_system.entity.Admin;
import com.unistay.housing_management_system.entity.HousingApplication;
import com.unistay.housing_management_system.entity.Student;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        StudentMapper.class,
        AdminMapper.class
})
public abstract class HousingApplicationMapper {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    public abstract HousingApplicationResponseDto toDto(HousingApplication housingApplication);
    public abstract List<HousingApplicationResponseDto> toDtoList(List<HousingApplication> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    public abstract HousingApplication toEntity(HousingApplicationRequestCreateDto dto);

    @AfterMapping
    protected void setCreateRelations(HousingApplicationRequestCreateDto dto,
                                      @MappingTarget HousingApplication entity) {
        Student student = studentRepository.findByUniversityId(dto.getUniversityId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        entity.setStudent(student);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documentationPath", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewDate", expression = "java(java.time.LocalDate.now())")
    public abstract void reviewEntityFromDto(HousingApplicationRequestReviewDto dto,
                                             @MappingTarget HousingApplication entity);

    @AfterMapping
    protected void setReviewRelations(HousingApplicationRequestReviewDto dto,
                                      @MappingTarget HousingApplication entity) {
        Admin admin = adminRepository.findById(dto.getReviewedById())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        entity.setReviewedBy(admin);
    }
}
