package com.unistay.housing_management_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unistay.housing_management_system.enums.HousingApplicationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "housing_applications")
public class HousingApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "housing_application_id")
    private Long id;

    @Column(name = "documentation_path")
    private String documentationPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HousingApplicationStatus status = HousingApplicationStatus.PENDING;

    @Column(name = "submission_date")
    private LocalDate submissionDate;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    @JsonIgnore
    private Admin reviewedBy;

    @PrePersist
    protected void onCreate() {
        if (submissionDate == null) {
            submissionDate = LocalDate.now();
        }
        if (status == null) {
            status = HousingApplicationStatus.PENDING;
        }
    }

}
