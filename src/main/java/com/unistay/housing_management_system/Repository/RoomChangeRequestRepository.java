package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.RoomChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomChangeRequestRepository extends JpaRepository<RoomChangeRequest, Integer> {

}
