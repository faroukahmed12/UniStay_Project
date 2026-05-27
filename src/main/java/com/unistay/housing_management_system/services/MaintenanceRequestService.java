package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.MaintenanceRequestRepository;
import com.unistay.housing_management_system.dtos.request.MaintenanceRequestCreateDto;
import com.unistay.housing_management_system.dtos.request.MaintenanceRequestUpdateDto;
import com.unistay.housing_management_system.dtos.response.MaintenanceResponseDto;
import com.unistay.housing_management_system.entity.Building;
import com.unistay.housing_management_system.entity.MaintenanceRequest;
import com.unistay.housing_management_system.entity.MaintenanceStaff;
import com.unistay.housing_management_system.entity.Room;
import com.unistay.housing_management_system.entity.Student;
import com.unistay.housing_management_system.enums.MaintenanceStatus;
import com.unistay.housing_management_system.exceptions.InvalidStatusTransitionException;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import com.unistay.housing_management_system.exceptions.UnauthorizedAccessException;
import com.unistay.housing_management_system.mapping.MaintenanceRequestMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceRequestService.class);

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final StudentService studentService;
    private final MaintenanceStaffService maintenanceStaffService;
    private final BuildingService buildingService;
    private final RoomService roomService;
    private final AuthService authService;
    private final MaintenanceRequestMapper maintenanceRequestMapper;

    @Transactional
    public MaintenanceResponseDto createRequest(MaintenanceRequestCreateDto dto) {
        logger.info("Student [{}] submitting maintenance request — issueType: {}, room: {}, building: {}",
                authService.getCurrentUserId(), dto.getIssueType(), dto.getRoomNumber(), dto.getBuildingName());

        Student student = studentService.getStudentById(authService.getCurrentUserId());
        Building building = buildingService.getBuildingById(dto.getBuildingId());
        Room room = roomService.getRoomById(dto.getRoomId());

        if (!room.getBuilding().getBuildingId().equals(building.getBuildingId())) {
            logger.warn("Room [{}] does not belong to building [{}]",
                    dto.getRoomId(), dto.getBuildingId());
            throw new IllegalArgumentException("Room [" + dto.getRoomNumber() + "] does not belong to building [" + dto.getBuildingName() + "].");
        }

        MaintenanceRequest request = new MaintenanceRequest();
        request.setStudent(student);
        request.setBuilding(building);
        request.setRoom(room);
        request.setIssueType(dto.getIssueType());
        request.setDescription(dto.getDescription());
        request.setStatus(MaintenanceStatus.PENDING);

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        logger.info("Maintenance request [{}] created successfully for student [{}] in room [{}]",
                saved.getId(), student.getId(), dto.getRoomNumber());

        return maintenanceRequestMapper.toDto(saved);
    }

    public List<MaintenanceResponseDto> getAllRequests() {
        logger.info("Fetching all maintenance requests");

        List<MaintenanceRequest> requests = maintenanceRequestRepository.findAll();
        logger.info("Total maintenance requests found: {}", requests.size());

        return maintenanceRequestMapper.toDtoList(requests);
    }

    @Transactional(readOnly = true)
    public MaintenanceResponseDto getRequestById(Long id) {
        logger.info("Fetching maintenance request with id: {}", id);
        return maintenanceRequestMapper.toDto(findRequestById(id));
    }

    public List<MaintenanceResponseDto> getRequestsByStatus(MaintenanceStatus status) {
        logger.info("Fetching maintenance requests with status: {}", status);

        List<MaintenanceRequest> requests = maintenanceRequestRepository.findAllByStatus(status);

        logger.info("Found {} requests with status: {}", requests.size(), status);

        return maintenanceRequestMapper.toDtoList(requests);
    }

    public List<MaintenanceResponseDto> getRequestsByStudentId(Long studentId) {
        logger.info("Fetching maintenance requests for student [{}]", studentId);

        List<MaintenanceRequest> requests = maintenanceRequestRepository.findAllByStudent_Id(studentId);

        logger.info("Found {} requests for student [{}]", requests.size(), studentId);

        return maintenanceRequestMapper.toDtoList(requests);
    }

    public List<MaintenanceResponseDto> getRequestsByStaffId(Long staffId) {
        logger.info("Fetching maintenance requests assigned to staff [{}]", staffId);

        List<MaintenanceRequest> requests = maintenanceRequestRepository.findAllByAssignedStaff_Id(staffId);

        logger.info("Found {} requests assigned to staff [{}]", requests.size(), staffId);

        return requests.stream()
                .map(maintenanceRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public long getPendingMaintenanceRequestsCount() {
        long count = maintenanceRequestRepository.countByStatus(MaintenanceStatus.PENDING);
        logger.info("Pending maintenance requests count: {}", count);
        return count;
    }

    @Transactional
    public MaintenanceResponseDto updateRequest(Long requestId, MaintenanceRequestUpdateDto dto) {
        logger.info("Update received for maintenance request [{}]", requestId);

        MaintenanceRequest request = findRequestById(requestId);

        if (request.getStatus() == MaintenanceStatus.COMPLETED) {
            logger.warn("Request [{}] is already COMPLETED — update rejected", requestId);
            throw new InvalidStatusTransitionException("Cannot update a request that is already COMPLETED.");
        }

        // authorization ─────────────────────────────────────────────
        Long currentUserId = authService.getCurrentUserId();
        boolean isAdmin = authService.isAdmin();

        // not admin & not assigned staff → reject
        if (!isAdmin) {
            if (request.getAssignedStaff() == null || !request.getAssignedStaff().getId().equals(currentUserId)) {
                logger.warn("Staff [{}] attempted to update request [{}] not assigned to them", currentUserId, requestId);
                throw new UnauthorizedAccessException("You can only update maintenance requests that are assigned to you.");
            }
        }

        // ── Field 1: assignedStaffId (admin only) ─────────────────────────────
        if (dto.getAssignedStaffId() != null) {
            if (!isAdmin) {
                logger.warn("Staff [{}] tried to reassign request [{}] — forbidden", currentUserId, requestId);
                throw new UnauthorizedAccessException("Only admins can reassign a maintenance request to a different staff member.");
            }

            MaintenanceStaff newStaff = maintenanceStaffService.getStaffById(dto.getAssignedStaffId());
            request.setAssignedStaff(newStaff);

            request.setStatus(MaintenanceStatus.IN_PROGRESS);

            logger.info("Request [{}] reassigned to staff [{}]", requestId, dto.getAssignedStaffId());
        }

        // ── Field 2: status (staff or admin can update) ─────────────────────────
        if (dto.getStatus() != null) {
            request.setStatus(dto.getStatus());
            logger.info("Request [{}] status set to {} by user [{}]", requestId, dto.getStatus(), currentUserId);

            if (dto.getStatus() == MaintenanceStatus.COMPLETED) {
                request.setResolvedDate(LocalDateTime.now());
                logger.info("Request [{}] resolvedDate auto-set to now()", requestId);
            }
        }

        // ── Field 3: notes ────────────────────────────────────────────────────
        if (dto.getNotes() != null && !dto.getNotes().isBlank()) {
            request.setNotes(dto.getNotes());
            logger.info("Request [{}] notes updated", requestId);
        }

        MaintenanceRequest updated = maintenanceRequestRepository.save(request);

        logger.info("Maintenance request [{}] updated successfully — final status: [{}]",
                requestId, updated.getStatus());

        return maintenanceRequestMapper.toDto(updated);
    }

    @Transactional
    public void deleteRequest(Long requestId) {
        logger.info("Deleting maintenance request [{}]", requestId);

        MaintenanceRequest request = findRequestById(requestId);
        maintenanceRequestRepository.delete(request);

        logger.info("Maintenance request [{}] deleted successfully", requestId);
    }

    // helper methods
    private MaintenanceRequest findRequestById(Long id) {
        return maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Maintenance request not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "Maintenance request not found with id: " + id);
                });
    }
}