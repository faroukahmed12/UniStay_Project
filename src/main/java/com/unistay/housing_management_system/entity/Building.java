package com.unistay.housing_management_system.entity;

import com.unistay.housing_management_system.enums.BuildingGenderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "building_name", nullable = false, length = 100)
    private String buildingName;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(name = "total_capacity")
    private Integer totalCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_type", nullable = false, length = 10)
    private BuildingGenderType genderType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceRequest> maintenanceRequests = new ArrayList<>();
}
