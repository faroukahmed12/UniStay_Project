package com.unistay.housing_management_system.entity;

import com.unistay.housing_management_system.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

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
}
