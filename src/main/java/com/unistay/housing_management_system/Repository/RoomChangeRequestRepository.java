package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.RoomChangeRequest;
import com.unistay.housing_management_system.enums.RoomChangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomChangeRequestRepository extends JpaRepository<RoomChangeRequest, Long> {

    boolean existsByStudent_IdAndStatus(Long id, RoomChangeStatus roomChangeStatus);

    List<RoomChangeRequest> findAllByStatus(RoomChangeStatus status);
}
