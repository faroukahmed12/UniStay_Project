package com.unistay.housing_management_system.entity;

import com.unistay.housing_management_system.enums.RoomChangeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room_change_requests")
public class RoomChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RoomChangeStatus status = RoomChangeStatus.PENDING;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Admin reviewedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_room_id")
    private Room requestedRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_room_id", nullable = false)
    private Room currentRoom;


    @PrePersist
    protected void onCreate() {
        if (requestDate == null) {
            requestDate = LocalDate.now();
        }
        if (status == null) {
            status = RoomChangeStatus.PENDING;
        }
    }
}

