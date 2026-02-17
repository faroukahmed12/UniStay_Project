package com.unistay.housing_management_system.entity;

import com.unistay.housing_management_system.enums.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "maintenance_requests")
public class MaintenanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "issue_type", nullable = false, length = 50)
    private String issueType;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private MaintenanceStatus status = MaintenanceStatus.PENDING;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private MaintenanceStaff assignedStaff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @PrePersist
    protected void onCreate() {
        if (submissionDate == null) {
            submissionDate = LocalDateTime.now();
        }
        if (status == null) {
            status = MaintenanceStatus.PENDING;
        }
    }
}
