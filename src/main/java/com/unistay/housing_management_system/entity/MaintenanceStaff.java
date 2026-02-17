package com.unistay.housing_management_system.entity;

import com.unistay.housing_management_system.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("MAINTENANCE_STAFF")
@PrimaryKeyJoinColumn(name = "id")
@Table(name="maintenance_staff")
public class MaintenanceStaff extends User {

    @Column(name = "specialization", length = 50)
    private String specialization;

    @OneToMany(mappedBy = "assignedStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceRequest> maintenanceRequestList = new ArrayList<>();

}
