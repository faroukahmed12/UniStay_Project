package com.unistay.housing_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@DiscriminatorValue("ADMIN")
@PrimaryKeyJoinColumn(name = "id")
@Table(name="admins")
public class Admin extends User {

    @Column(name="department",length = 50)
    private String department;

    @OneToMany(mappedBy = "reviewedBy", fetch = FetchType.LAZY)
    private List<HousingApplication> reviewedApplications = new ArrayList<>();

    @OneToMany(mappedBy = "assignedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomAssignment> assignedRooms = new ArrayList<>();

    @OneToMany(mappedBy = "reviewedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomChangeRequest> reviewedRoomChanges = new ArrayList<>();
}
