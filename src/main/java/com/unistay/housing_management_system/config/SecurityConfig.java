package com.unistay.housing_management_system.config;

import com.unistay.housing_management_system.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ─── Swagger ──────
                        .requestMatchers("/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html").permitAll()

                        // ─── Auth (Login / Register) ─────────
                        .requestMatchers("/api/auth/current-user").hasAnyRole("ADMIN", "STUDENT", "MAINTENANCE_STAFF")
                        .requestMatchers("/api/auth/**").permitAll()

                        // ─── Users ─────────────────────────────────────────────────
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // ─── Admins ────────────────────────────────────────────────
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")

                        // ─── Students ──────────────────────────────────────────────
                        .requestMatchers("/api/students/**").hasRole("ADMIN")

                        // ─── Buildings ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/buildings").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/buildings/**").hasRole("ADMIN")

                        // ─── Rooms ─────────────────────────────────────────────────
                        .requestMatchers("/api/rooms/available").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/rooms/building/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/rooms/**").hasRole("ADMIN")

                        // ─── Room Assignments ──────────────────────────────────────
                        .requestMatchers("/api/room-assignments/count/building/{buildingId}").hasRole("ADMIN")
                        .requestMatchers("/api/room-assignments/student/{studentId}/active").hasAnyRole("STUDENT","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/room-assignments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/room-assignments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/room-assignments/*/move-out").hasRole("ADMIN")

                        .requestMatchers("/api/room-assignments/student/*/active",
                                "/api/room-assignments/student/*/history").hasAnyRole("ADMIN", "STUDENT")

                        // ─── Housing Applications ──────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/housing-applications/download/{applicationId}").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/housing-applications").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/housing-applications").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/housing-applications/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/housing-applications/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/housing-applications/*").hasRole("ADMIN")
                        .requestMatchers("/api/housing-applications/student/**").hasAnyRole("ADMIN", "STUDENT")

                        // ─── Room Change Requests ──────────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/room-change-requests").hasRole("STUDENT")
                        .requestMatchers("/api/room-change-requests/**").hasRole("ADMIN")

                        // ─── Maintenance Requests ──────────────────────────────────

                        .requestMatchers(HttpMethod.POST, "/api/maintenance-requests").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/maintenance-requests").hasAnyRole("MAINTENANCE_STAFF","ADMIN")
                        .requestMatchers("/api/maintenance-requests/*/",
                                "/api/maintenance-requests/staff/**").hasAnyRole("ADMIN", "MAINTENANCE_STAFF")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/maintenance-requests/*").hasAnyRole("ADMIN", "MAINTENANCE_STAFF")
                        .requestMatchers("/api/maintenance-requests/student/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/maintenance-requests/*").hasRole("ADMIN")

                        // ─── Maintenance Staff ──────────────────────────────
                        .requestMatchers("/api/maintenance-staff/**").hasRole("ADMIN")

                        // ─── Dashboard ───────────────────────────────────────────────
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")

                        // any other endpoints
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}