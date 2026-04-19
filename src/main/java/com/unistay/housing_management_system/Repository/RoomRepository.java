package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.Room;
import com.unistay.housing_management_system.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    boolean existsByRoomNumber(String roomNumber);

    List<Room> findByStatus(RoomStatus roomStatus);

    List<Room> findByBuilding_BuildingId(Long buildingId);
}
