package com.unistay.housing_management_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.unistay.housing_management_system.enums.UserType;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name = "name", nullable = false,length = 100)
    private String name;

    @Column(name="email", nullable = false, unique = true,length = 100)
    private String email;

    @Column(name="phone", length = 20)
    private String phone;

    //@JsonIgnore  // لا يُعاد في JSON response
    @Column(name="hashed_password")
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, insertable = false, updatable = false)
    private UserType userType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
