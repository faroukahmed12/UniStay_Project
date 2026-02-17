package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.RoomAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, Long> {
}
