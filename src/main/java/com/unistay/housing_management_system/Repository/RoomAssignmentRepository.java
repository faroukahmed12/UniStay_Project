package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.RoomAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, Long> {
    boolean existsByStudent_IdAndMoveOutDateIsNull(Long id);

    Optional<RoomAssignment> findByStudent_IdAndMoveOutDateIsNull(Long studentId);

    List<RoomAssignment> findAllByStudent_Id(Long studentId);

    boolean existsByStudent_IdAndRoom_RoomIdAndMoveOutDateIsNull(Long id, Long roomId);
}
