package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.RoomRepository;
import com.unistay.housing_management_system.dtos.request.RoomRequestDto;
import com.unistay.housing_management_system.dtos.response.RoomResponseDto;
import com.unistay.housing_management_system.entity.Building;
import com.unistay.housing_management_system.entity.Room;
import com.unistay.housing_management_system.enums.RoomStatus;
import com.unistay.housing_management_system.exceptions.ResourceAlreadyExistsException;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import com.unistay.housing_management_system.mapping.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final BuildingService buildingService;
    private final RoomMapper roomMapper;

    @Transactional
    public RoomResponseDto createRoom(RoomRequestDto dto) {
        logger.info("Attempting to create room number: {} in building id: {}", dto.getRoomNumber(), dto.getBuildingId());

        if (roomRepository.existsByRoomNumber(dto.getRoomNumber())) {
            logger.warn("Room already exists with number: {}", dto.getRoomNumber());
            throw new ResourceAlreadyExistsException("Room already exists with number: " + dto.getRoomNumber());
        }

        Building building = buildingService.getBuildingById(dto.getBuildingId());

        Room room = roomMapper.toEntity(dto);
        room.setBuilding(building);
        room.setOccupiedBeds(0);
        room.setStatus(RoomStatus.AVAILABLE);

        Room saved = roomRepository.save(room);
        logger.info("Room created successfully with id: {}", saved.getRoomId());

        return roomMapper.toDto(saved);
    }

    public List<RoomResponseDto> getAllRooms() {
        logger.info("Fetching all rooms");

        List<Room> rooms = roomRepository.findAll();
        logger.info("Total rooms found: {}", rooms.size());

        return roomMapper.toDtoList(rooms);
    }

    public RoomResponseDto getRoomDtoById(Long id) {
        logger.info("Fetching room with id: {}", id);
        return roomMapper.toDto(getRoomById(id));
    }

    public List<RoomResponseDto> getRoomsByStatus(RoomStatus status) {
        logger.info("Fetching rooms with status: {}", status);
        List<Room> rooms = roomRepository.findByStatus(status);
        logger.info("Found {} rooms with status: {}", rooms.size(), status);
        return roomMapper.toDtoList(rooms);
    }

    public List<RoomResponseDto> getRoomsByBuilding(Long buildingId) {
        logger.info("Fetching rooms for building id: {}", buildingId);

        buildingService.getBuildingById(buildingId);

        List<Room> rooms = roomRepository.findByBuilding_BuildingId(buildingId);
        logger.info("Found {} rooms in building id: {}", rooms.size(), buildingId);

        return roomMapper.toDtoList(rooms);
    }

    @Transactional
    public RoomResponseDto updateRoom(Long id, RoomRequestDto dto) {
        logger.info("Updating room with id: {}", id);

        Room room = getRoomById(id);

        room.setRoomNumber(dto.getRoomNumber());
        room.setCapacity(dto.getCapacity());

        // Recalculate status based on updated capacity vs current occupiedBeds
        recalculateRoomStatus(room);

        Room updated = roomRepository.save(room);
        logger.info("Room updated successfully with id: {}", id);

        return roomMapper.toDto(updated);
    }

    @Transactional
    public void deleteRoom(Long id) {
        logger.info("Deleting room with id: {}", id);

        Room room = getRoomById(id);

        // Guard: cannot delete a room that has students
        if (room.getOccupiedBeds() != null && room.getOccupiedBeds() > 0) {
            logger.warn("Cannot delete room [{}] — it has {} occupied beds", id, room.getOccupiedBeds());
            throw new IllegalStateException(
                    "Cannot delete room with occupied beds. Please reassign students first.");
        }

        roomRepository.delete(room);
        logger.info("Room [{}] deleted successfully", id);
    }

    // helpers methods
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Room not found with id: {}", id);
                    return new ResourceNotFoundException("Room not found with id: " + id);
                });
    }

    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> {
                    logger.warn("Room not found with number: {} ", roomNumber);
                    return new ResourceNotFoundException("Room not found with number: " + roomNumber);
                });
    }

    /**
     * Increments occupiedBeds by 1 after a student is assigned.
     * Sets status to FULL when capacity is reached.
     */
    @Transactional
    public void incrementOccupiedBeds(Room room) {
        int current = room.getOccupiedBeds() == null ? 0 : room.getOccupiedBeds();

        if (current >= room.getCapacity()) {
            logger.warn("Room [{}] is already full — capacity: {}", room.getRoomId(), room.getCapacity());
            throw new IllegalStateException("Room is already at full capacity.");
        }

        room.setOccupiedBeds(current + 1);
        recalculateRoomStatus(room);
        roomRepository.save(room);

        logger.info("Room [{}] occupiedBeds incremented to {}", room.getRoomId(), room.getOccupiedBeds());
    }

    /**
     * Decrements occupiedBeds by 1 after a student is removed.
     * Sets status back to AVAILABLE when beds are freed.
     */
    @Transactional
    public void decrementOccupiedBeds(Room room) {
        int current = room.getOccupiedBeds() == null ? 0 : room.getOccupiedBeds();

        if (current <= 0) {
            logger.warn("Room [{}] already has 0 occupied beds — cannot decrement", room.getRoomId());
            throw new IllegalStateException("Room occupied beds count is already 0.");
        }

        room.setOccupiedBeds(current - 1);
        recalculateRoomStatus(room);
        roomRepository.save(room);

        logger.info("Room [{}] occupiedBeds decremented to {}", room.getRoomId(), room.getOccupiedBeds());
    }

    private void recalculateRoomStatus(Room room) {
        int occupied = room.getOccupiedBeds() == null ? 0 : room.getOccupiedBeds();

        if (occupied >= room.getCapacity()) {
            room.setStatus(RoomStatus.OCCUPIED);
        } else {
            room.setStatus(RoomStatus.AVAILABLE);
        }
    }
}