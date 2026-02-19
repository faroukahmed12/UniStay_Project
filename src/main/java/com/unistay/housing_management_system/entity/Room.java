package com.unistay.housing_management_system.entity;

import com.unistay.housing_management_system.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "room_number", unique = true, nullable = false, length = 20)
    private String roomNumber;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "occupied_beds")
    private Integer occupiedBeds = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomAssignment> roomAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "requestedRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomChangeRequest> requestedRoomChanges = new ArrayList<>();

    @OneToMany(mappedBy = "currentRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomChangeRequest> currentRoomChanges = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceRequest> maintenanceRequests = new ArrayList<>();

    /*
    public Integer getAvailableBeds() {
        return capacity - (occupiedBeds != null ? occupiedBeds : 0);
    }

    public boolean hasAvailableBeds() {
        return getAvailableBeds() > 0;
    }
     */
}

