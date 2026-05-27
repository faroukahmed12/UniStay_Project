package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.RoomAssignmentRepository;
import com.unistay.housing_management_system.dtos.request.RoomAssignmentCreateDto;
import com.unistay.housing_management_system.dtos.response.RoomAssignmentResponseDto;
import com.unistay.housing_management_system.entity.Admin;
import com.unistay.housing_management_system.entity.Room;
import com.unistay.housing_management_system.entity.RoomAssignment;
import com.unistay.housing_management_system.entity.Student;
import com.unistay.housing_management_system.enums.RoomStatus;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import com.unistay.housing_management_system.mapping.RoomAssignmentMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(RoomAssignmentService.class);

    private final RoomAssignmentRepository roomAssignmentRepository;
    private final StudentService studentService;
    private final RoomService roomService;
    private final AdminService adminService;
    private final AuthService authService;
    private final RoomAssignmentMapper roomAssignmentMapper;

    @Transactional
    public RoomAssignmentResponseDto assignRoom(RoomAssignmentCreateDto dto) {
        Room room = roomService.getRoomByNumber(dto.getRoomNumber());
        Student student = studentService.getStudentByUniversityId(dto.getUniversityId());

        // Optional validation: if frontend sends buildingId, ensure the room belongs to that building
        if (dto.getBuildingId() != null
                && (room.getBuilding() == null
                || room.getBuilding().getBuildingId() == null
                || !dto.getBuildingId().equals(room.getBuilding().getBuildingId()))) {
            throw new IllegalStateException(
                    "Room [" + dto.getRoomNumber() + "] does not belong to building id: " + dto.getBuildingId());
        }

        logger.info("Assigning room [{}] to student [{}]", room.getRoomId(), dto.getUniversityId());

        if (room.getStatus() == RoomStatus.OCCUPIED) {
            logger.warn("Room [{}] is full — cannot assign", room.getRoomId());
            throw new IllegalStateException(
                    "Room [" + room.getRoomNumber() + "] is full. Please choose another room.");
        }

        boolean alreadyAssigned = roomAssignmentRepository.existsByStudent_IdAndMoveOutDateIsNull(student.getId());
        if (alreadyAssigned) {
            logger.warn("Student [{}] already has an active room assignment", student.getId());
            throw new IllegalStateException(
                    "Student already has an active room assignment. " +
                            "Please process a room change request instead.");
        }

        Long adminId = authService.getCurrentUserId();
        Admin admin  = adminService.getAdminById(adminId);

        RoomAssignment assignment = new RoomAssignment();
        assignment.setRoom(room);
        assignment.setBuilding(room.getBuilding());
        assignment.setStudent(student);
        assignment.setAssignedBy(admin);
        assignment.setAssignmentDate(LocalDate.now());
        assignment.setMoveInDate(dto.getMoveInDate());

        roomAssignmentRepository.save(assignment);

        roomService.incrementOccupiedBeds(room);

        logger.info("Room [{}] assigned to student [{}] by admin [{}]",
                room.getRoomId(), student.getId(), adminId);

        return roomAssignmentMapper.toDto(assignment);
    }

    @Transactional(readOnly = true)
    public List<RoomAssignmentResponseDto> getAllAssignments() {
        logger.info("Fetching all room assignments");

        List<RoomAssignment> assignments = roomAssignmentRepository.findAll();
        logger.info("Total assignments found: {}", assignments.size());

        return roomAssignmentMapper.toDtoList(assignments);
    }

    @Transactional(readOnly = true)
    public RoomAssignmentResponseDto getActiveAssignmentByStudentId(Long studentId) {
        logger.info("Fetching active assignment for student [{}]", studentId);

        RoomAssignment assignment = roomAssignmentRepository.findByStudent_IdAndMoveOutDateIsNull(studentId)
                .orElseThrow(() -> {
                    logger.warn("No active assignment found for student [{}]", studentId);
                    return new ResourceNotFoundException(
                            "No active room assignment found for student id: " + studentId);
                });

        return roomAssignmentMapper.toDto(assignment);
    }

    @Transactional(readOnly = true)
    public List<RoomAssignmentResponseDto> getAssignmentHistoryByStudentId(Long studentId) {
        logger.info("Fetching assignment history for student [{}]", studentId);

        List<RoomAssignment> assignments = roomAssignmentRepository.findAllByStudent_Id(studentId);

        logger.info("Found {} assignments for student [{}]", assignments.size(), studentId);

        return roomAssignmentMapper.toDtoList(assignments);
    }

    @Transactional
    public RoomAssignmentResponseDto moveOutStudent(Long assignmentId) {
        logger.info("Processing move-out for assignment [{}]", assignmentId);

        RoomAssignment assignment = roomAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    logger.warn("Assignment [{}] not found", assignmentId);
                    return new ResourceNotFoundException(
                            "Room assignment not found with id: " + assignmentId);
                });

        // Guard: assignment must still be active
        if (assignment.getMoveOutDate() != null) {
            logger.warn("Assignment [{}] is already closed (moveOutDate: {})", assignmentId, assignment.getMoveOutDate());
            throw new IllegalStateException("This assignment is already closed.");
        }

        assignment.setMoveOutDate(LocalDate.now());
        roomAssignmentRepository.save(assignment);

        // Free the bed in the room
        roomService.decrementOccupiedBeds(assignment.getRoom());

        logger.info("Student [{}] moved out from room [{}]",
                assignment.getStudent().getId(), assignment.getRoom().getRoomId());

        return roomAssignmentMapper.toDto(assignment);
    }

    public long countActiveStudentsByBuilding(Long buildingId) {
        logger.info("Counting active students in building [{}]", buildingId);
        return roomAssignmentRepository.countActiveStudentsByBuildingBuildingId(buildingId);
    }

}