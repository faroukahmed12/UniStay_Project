package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.HousingApplicationRepository;
import com.unistay.housing_management_system.dtos.request.HousingApplicationRequestCreateDto;
import com.unistay.housing_management_system.dtos.response.HousingApplicationResponseDto;
import com.unistay.housing_management_system.entity.Admin;
import com.unistay.housing_management_system.entity.HousingApplication;
import com.unistay.housing_management_system.entity.Student;
import com.unistay.housing_management_system.enums.HousingApplicationStatus;
import com.unistay.housing_management_system.exceptions.InvalidStatusTransitionException;
import com.unistay.housing_management_system.exceptions.ResourceAlreadyExistsException;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import com.unistay.housing_management_system.mapping.HousingApplicationMapperImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HousingApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(HousingApplicationService.class);
    private final StudentService studentService;
    private final AdminService adminService;
    private final HousingApplicationMapperImpl housingApplicationMapper;
    private final HousingApplicationRepository housingApplicationRepository;
    private final AuthService authService;

    @Transactional
    public HousingApplicationResponseDto createHousingApplication(HousingApplicationRequestCreateDto dto) {
        // check if student exists
        Student student = studentService.getStudentByUniversityId(dto.getUniversityId());

        // check if student has a pending application
        boolean hasPendingApplication = hasPendingApplication(student);
        if (hasPendingApplication) {
            logger.warn("Student [{}] already has a pending housing application", student.getId());
            throw new ResourceAlreadyExistsException("Student already has a pending housing application");
        }

        // create housing application
        HousingApplication housingApplication = housingApplicationMapper.toEntity(dto);
        housingApplication.setStudent(student);
        housingApplicationRepository.save(housingApplication);
        logger.info("Created housing application for student with university ID: {}", dto.getUniversityId());

        return housingApplicationMapper.toDto(housingApplication);
    }

    public List<HousingApplicationResponseDto> getAllHousingApplications() {
        List<HousingApplication> applications = housingApplicationRepository.findAll();
        logger.info("Fetched all housing applications, total count: {}", applications.size());
        return applications.stream()
                .map(housingApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    public HousingApplicationResponseDto getHousingApplicationById(Long id) {
        HousingApplication application = housingApplicationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Housing application not found with id={}", id);
                    return new ResourceNotFoundException("Housing application not found with id: " + id);
                });
        logger.info("Fetched housing application with id: {}", id);
        return housingApplicationMapper.toDto(application);
    }

    public List<HousingApplicationResponseDto> getHousingApplicationsByStudentUniversityId(String universityId) {
        Student student = studentService.getStudentByUniversityId(universityId);
        List<HousingApplication> applications = housingApplicationRepository.findByStudentId(student.getId());
        logger.info("Fetched {} housing applications for student with university ID: {}", applications.size(), universityId);
        return applications.stream()
                .map(housingApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public HousingApplicationResponseDto updateHousingApplicationStatus(Long applicationId,
                                                                        HousingApplicationStatus newStatus) {
        logger.info("Admin updating status of application [{}] to [{}]", applicationId, newStatus);

        HousingApplication application = housingApplicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    logger.warn("Housing application not found with id={}", applicationId);
                    return new ResourceNotFoundException("Housing application not found with id: " + applicationId);
                });

        validateStatusTransition(application.getStatus(), newStatus);

        Long currentAdmin = authService.getCurrentUserId();
        Admin admin = adminService.getAdminById(currentAdmin);

        application.setStatus(newStatus);
        application.setReviewDate(LocalDate.now());
        application.setReviewedBy(admin);
        housingApplicationRepository.save(application);
        logger.info("Updated housing application status for application id: {} to {} by {}", applicationId, newStatus, admin.getName());

        return housingApplicationMapper.toDto(application);
    }

    @Transactional
    public void deleteApplication(Long applicationId) {

        logger.info("Deleting housing application with id={}", applicationId);

        HousingApplication application = housingApplicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    logger.warn("Housing application not found with id={}", applicationId);
                    return new ResourceNotFoundException("Housing application not found with id: " + applicationId);
                });

        housingApplicationRepository.delete(application);

        logger.info("Housing application deleted successfully with id={}", applicationId);
    }


    // Helper Methods -------------
    private boolean hasPendingApplication(Student student) {
        return housingApplicationRepository.existsByStudentIdAndStatus(
                student.getId(),
                HousingApplicationStatus.PENDING
        );
    }

    private void validateStatusTransition(HousingApplicationStatus current, HousingApplicationStatus next) {
        if (current == HousingApplicationStatus.APPROVED || current == HousingApplicationStatus.REJECTED) {
            logger.warn("Illegal transition attempt: {} → {}", current, next);
            throw new InvalidStatusTransitionException(
                    String.format("Cannot change status from [%s]. " +
                            "This application is already finalized.", current));
        }
        if (current == next) {
            throw new InvalidStatusTransitionException("Application is already in status: " + current);
        }
    }
}

 /*
    public Page<HousingApplication> getAllApplications(int page,
                                                       int size,
                                                       String sortBy,
                                                       String direction) {

        logger.info("Fetching housing applications - page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<HousingApplication> applications = applicationRepository.findAll(pageable);

        log.debug("Total applications found: {}", applications.getTotalElements());

        return applications;
    }
    */