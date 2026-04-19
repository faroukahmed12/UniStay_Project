package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.MaintenanceStaffRepository;
import com.unistay.housing_management_system.dtos.response.MaintenanceStaffDto;
import com.unistay.housing_management_system.entity.MaintenanceStaff;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import com.unistay.housing_management_system.mapping.MaintenanceStaffMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceStaffService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceStaffService.class);

    private final MaintenanceStaffRepository maintenanceStaffRepository;
    private final MaintenanceStaffMapper maintenanceStaffMapper;

    @Transactional(readOnly = true)
    public List<MaintenanceStaffDto> getAllStaff() {
        logger.info("Fetching all maintenance staff");

        List<MaintenanceStaff> staffList = maintenanceStaffRepository.findAll();
        logger.info("Total maintenance staff found: {}", staffList.size());

        return maintenanceStaffMapper.toDtoList(staffList);
    }

    @Transactional(readOnly = true)
    public MaintenanceStaffDto getStaffDtoById(Long id) {
        logger.info("Fetching maintenance staff with id: {}", id);
        return maintenanceStaffMapper.toDto(getStaffById(id));
    }

    @Transactional(readOnly = true)
    public List<MaintenanceStaffDto> getStaffBySpecialization(String specialization) {
        logger.info("Fetching maintenance staff with specialization: {}", specialization);

        List<MaintenanceStaff> staffList =
                maintenanceStaffRepository.findBySpecialization(specialization);

        logger.info("Found {} staff with specialization: {}", staffList.size(), specialization);

        return maintenanceStaffMapper.toDtoList(staffList);
    }

    @Transactional
    public void deactivateStaff(Long id) {
        logger.info("Deactivating maintenance staff with id: {}", id);

        MaintenanceStaff staff = getStaffById(id);

        if (Boolean.FALSE.equals(staff.getIsActive())) {
            logger.warn("Maintenance staff [{}] is already inactive", id);
            throw new IllegalStateException("Maintenance staff is already inactive.");
        }

        staff.setIsActive(false);
        maintenanceStaffRepository.save(staff);
        logger.info("Maintenance staff [{}] deactivated successfully", id);
    }

    //  helper methods
    public MaintenanceStaff getStaffById(Long id) {
        return maintenanceStaffRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Maintenance staff not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "Maintenance staff not found with id: " + id);
                });
    }
}