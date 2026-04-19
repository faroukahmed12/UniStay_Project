package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.RoomAssignmentRepository;
import com.unistay.housing_management_system.Repository.RoomChangeRequestRepository;
import com.unistay.housing_management_system.Repository.RoomRepository;
import com.unistay.housing_management_system.dtos.request.RoomChangeRequestCreateDto;
import com.unistay.housing_management_system.dtos.request.RoomChangeRequestUpdateDto;
import com.unistay.housing_management_system.dtos.response.RoomChangeResponseDto;
import com.unistay.housing_management_system.entity.Admin;
import com.unistay.housing_management_system.entity.Room;
import com.unistay.housing_management_system.entity.RoomAssignment;
import com.unistay.housing_management_system.entity.RoomChangeRequest;
import com.unistay.housing_management_system.entity.Student;
import com.unistay.housing_management_system.enums.RoomChangeStatus;
import com.unistay.housing_management_system.enums.RoomStatus;
import com.unistay.housing_management_system.exceptions.InvalidStatusTransitionException;
import com.unistay.housing_management_system.exceptions.ResourceAlreadyExistsException;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import com.unistay.housing_management_system.mapping.RoomChangeRequestMapper;
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
public class RoomChangeRequestService {

    private static final Logger logger = LoggerFactory.getLogger(RoomChangeRequestService.class);

    private final RoomChangeRequestRepository roomChangeRequestRepository;
    private final RoomAssignmentRepository roomAssignmentRepository;
    private final AuthService authService;
    private final StudentService studentService;
    private final AdminService adminService;
    private final RoomService roomService;
    private final RoomChangeRequestMapper roomChangeRequestMapper;

    @Transactional
    public RoomChangeResponseDto createRequest(RoomChangeRequestCreateDto dto) {
        logger.info("Room change request submitted — studentUniversityId: {}, currentRoom: {}, requestedRoom: {}",
                dto.getStudentUniversityId(), dto.getCurrentRoomNumber(), dto.getRequestedRoomNumber());

        Student student = studentService.getStudentByUniversityId(dto.getStudentUniversityId());
        Room currentRoom = roomService.getRoomByNumber(dto.getCurrentRoomNumber());

        // ── Guard: student must have an active assignment in the specified current room ──
        boolean hasActiveAssignmentInRoom = roomAssignmentRepository
                .existsByStudent_IdAndRoom_RoomIdAndMoveOutDateIsNull(student.getId(), currentRoom.getRoomId());
        if (!hasActiveAssignmentInRoom) {
            logger.warn("Student [{}] has no active assignment in room [{}]", student.getId(), dto.getCurrentRoomNumber());
            throw new ResourceNotFoundException("Student does not have an active room assignment in room: " + dto.getCurrentRoomNumber());
        }

        // ── Guard: student cannot have more than one PENDING request at a time ──
        boolean hasPendingRequest = roomChangeRequestRepository.existsByStudent_IdAndStatus(student.getId(), RoomChangeStatus.PENDING);
        if (hasPendingRequest) {
            logger.warn("Student [{}] already has a PENDING room change request", student.getId());
            throw new ResourceAlreadyExistsException(
                    "Student already has a pending room change request. "
                            + "Please wait for a decision before submitting a new one.");
        }

        // ── Resolve requested room (optional) ───
        Room requestedRoom = null;
        if (dto.getRequestedRoomNumber() != null && !dto.getRequestedRoomNumber().isBlank()) {
            requestedRoom = roomService.getRoomByNumber(dto.getRequestedRoomNumber());

            // Guard: cannot request the same room
            if (requestedRoom.getRoomId().equals(currentRoom.getRoomId())) {
                logger.warn("Student [{}] requested the same room [{}] they are currently in",
                        student.getId(), dto.getCurrentRoomNumber());
                throw new IllegalArgumentException(
                        "Requested room cannot be the same as the current room.");
            }

            // Guard: requested room must have available beds
            if (requestedRoom.getStatus() == RoomStatus.OCCUPIED) {
                logger.warn("Requested room [{}] is full — cannot submit request", requestedRoom.getRoomId());
                throw new IllegalStateException("Requested room [" + dto.getRequestedRoomNumber() + "] is full. "
                                                        + "Please choose another room or leave the field empty.");
            }
        }

        RoomChangeRequest request = new RoomChangeRequest();
        request.setStudent(student);
        request.setCurrentRoom(currentRoom);
        request.setRequestedRoom(requestedRoom);
        request.setReason(dto.getReason());
        request.setStatus(RoomChangeStatus.PENDING);

        RoomChangeRequest saved = roomChangeRequestRepository.save(request);
        logger.info("Room change request [{}] created for student [{}]", saved.getRequestId(), student.getId());
        return roomChangeRequestMapper.toDto(saved);
    }

    public List<RoomChangeResponseDto> getAllRequests() {
        logger.info("Fetching all room change requests");
        List<RoomChangeRequest> requests = roomChangeRequestRepository.findAll();
        logger.info("Total room change requests found: {}", requests.size());
        return roomChangeRequestMapper.toDtoList(requests);
    }

    public RoomChangeResponseDto getRequestById(Long requestId) {
        logger.info("Fetching room change request with id: {}", requestId);
        return roomChangeRequestMapper.toDto(findRequestById(requestId));
    }

    public List<RoomChangeResponseDto> getRequestsByStatus(RoomChangeStatus status) {
        logger.info("Fetching room change requests with status: {}", status);
        List<RoomChangeRequest> requests = roomChangeRequestRepository.findAllByStatus(status);
        logger.info("Found {} requests with status: {}", requests.size(), status);
        return roomChangeRequestMapper.toDtoList(requests);
    }

    @Transactional
    public RoomChangeResponseDto updateRequest(Long requestId, RoomChangeRequestUpdateDto dto) {
        logger.info("Reviewing room change request [{}] — decision: {}", requestId, dto.getStatus());

        RoomChangeRequest request = findRequestById(requestId);

        if (request.getStatus() != RoomChangeStatus.PENDING) {
            logger.warn("Request [{}] is already finalized with status: {}", requestId, request.getStatus());
            throw new InvalidStatusTransitionException("Cannot update a request that is already " + request.getStatus() + ".");
        }

        Long currentAdminId = authService.getCurrentUserId();
        Admin admin = adminService.getAdminById(currentAdminId);

        logger.info("Request [{}] being reviewed by admin [{}]", requestId, currentAdminId);

            // APPROVED Request
        if (dto.getStatus() == RoomChangeStatus.APPROVED) {
            handleApproval(request, dto, admin);
        }
            // REJECTED Request
        else if (dto.getStatus() == RoomChangeStatus.REJECTED) {
            handleRejection(request, admin);
        }
        else {
            logger.warn("Invalid review status [{}] for request [{}]", dto.getStatus(), requestId);
            throw new InvalidStatusTransitionException("Only APPROVED or REJECTED are valid review decisions.");
        }

        RoomChangeRequest updated = roomChangeRequestRepository.save(request);
        logger.info("Room change request [{}] finalized — status: {}", requestId, updated.getStatus());
        return roomChangeRequestMapper.toDto(updated);
    }

    @Transactional
    public void deleteRequest(Long requestId) {
        logger.info("Deleting room change request [{}]", requestId);

        RoomChangeRequest request = findRequestById(requestId);

        if (request.getStatus() != RoomChangeStatus.PENDING) {
            logger.warn("Cannot delete request [{}] with status: {}", requestId, request.getStatus());
            throw new InvalidStatusTransitionException("Only PENDING requests can be deleted.");
        }

        roomChangeRequestRepository.delete(request);
        logger.info("Room change request [{}] deleted successfully", requestId);
    }

    // Helper Methods
    private void handleApproval(RoomChangeRequest request, RoomChangeRequestUpdateDto dto, Admin admin) {

        Long requestId = request.getRequestId();
        Room targetRoom;

        // Determine target room: admin override > student's requested room
        if (dto.getRequestedRoomNumber() != null && !dto.getRequestedRoomNumber().isBlank()) {
            targetRoom = roomService.getRoomByNumber(dto.getRequestedRoomNumber());
            logger.info("Request [{}] — Admin overriding target room to [{}]", requestId, dto.getRequestedRoomNumber());
        }
        else if (request.getRequestedRoom() != null) {
            targetRoom = request.getRequestedRoom();
            logger.info("Request [{}] — Approving with student's requested room [{}]",
                    requestId, targetRoom.getRoomNumber());
        } else {
            logger.warn("Request [{}] cannot be approved — no target room available", requestId);
            throw new IllegalArgumentException("Cannot approve this request: no target room was specified by the student. ");
        }

        // target room must not be the same as the current room
        if (targetRoom.getRoomId().equals(request.getCurrentRoom().getRoomId())) {
            logger.warn("Request [{}] — target room is the same as current room", requestId);
            throw new IllegalArgumentException("Target room cannot be the same as the student's current room.");
        }

        // target room must have available beds
        if (targetRoom.getStatus() == RoomStatus.OCCUPIED) {
            logger.warn("Request [{}] — target room [{}] is full", requestId, targetRoom.getRoomId());
            throw new IllegalStateException("Target room [" + targetRoom.getRoomNumber() + "] is full.");
        }

        Room oldRoom = request.getCurrentRoom();
        // Transfer occupancy: free old room, occupy new room
        roomService.decrementOccupiedBeds(oldRoom);
        roomService.incrementOccupiedBeds(targetRoom);

        // Update the student's active RoomAssignment to the new room
        RoomAssignment activeAssignment = roomAssignmentRepository.findByStudent_IdAndMoveOutDateIsNull(request.getStudent().getId())
                .orElseThrow(() -> {
                    logger.warn("No active RoomAssignment found for student [{}] during approval", request.getStudent().getId());
                    return new ResourceNotFoundException("No active room assignment found for student. Cannot complete transfer.");
                });
        activeAssignment.setRoom(targetRoom);
        roomAssignmentRepository.save(activeAssignment);

        logger.info("Student [{}] transferred from room [{}] to room [{}]",
                request.getStudent().getId(),
                oldRoom.getRoomNumber(),
                targetRoom.getRoomNumber());

        request.setStatus(RoomChangeStatus.APPROVED);
        request.setRequestedRoom(targetRoom);
        request.setReviewedBy(admin);
        request.setReviewDate(LocalDate.now());
    }

    private void handleRejection(RoomChangeRequest request, Admin admin) {
        request.setStatus(RoomChangeStatus.REJECTED);
        request.setReviewedBy(admin);
        request.setReviewDate(LocalDate.now());
        logger.info("Room change request [{}] rejected by admin [{}]", request.getRequestId(), admin.getId());
    }

    private RoomChangeRequest findRequestById(Long id) {
        return roomChangeRequestRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Room change request not found with id: {}", id);
                    return new ResourceNotFoundException("Room change request not found with id: " + id);
                });
    }
}