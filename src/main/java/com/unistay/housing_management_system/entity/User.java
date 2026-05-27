package com.unistay.housing_management_system.entity;

import com.unistay.housing_management_system.enums.GenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.unistay.housing_management_system.enums.UserType;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name="users")
public class User implements UserDetails {
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
    @Column(name = "gender", nullable = false)
    private GenderType gender;

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


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userType.name()));
    }

    @Override
    public String getUsername() {
        return this.email; // Spring Security هيستخدم الـ email كـ username
    }

    @Override
    public String getPassword() {
        return this.hashedPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(isActive);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }

}
