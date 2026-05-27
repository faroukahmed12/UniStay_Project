package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.Repository.StudentRepository;
import com.unistay.housing_management_system.Repository.UserRepository;
import com.unistay.housing_management_system.dtos.request.LoginRequestDto;
import com.unistay.housing_management_system.dtos.response.LoginResponseDto;
import com.unistay.housing_management_system.dtos.response.StudentDto;
import com.unistay.housing_management_system.dtos.response.UserDto;
import com.unistay.housing_management_system.entity.Student;
import com.unistay.housing_management_system.entity.User;
import com.unistay.housing_management_system.mapping.StudentMapper;
import com.unistay.housing_management_system.mapping.UserMapper;
import com.unistay.housing_management_system.security.JwtUtil;
import com.unistay.housing_management_system.security.TokenBlacklistService;
import com.unistay.housing_management_system.services.AuthService;
import com.unistay.housing_management_system.services.StudentService;
import com.unistay.housing_management_system.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final AuthService authService;
    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        User user = userRepository.findByEmail(loginDto.email()).orElseThrow(
                () -> new RuntimeException("User not found"));

        // Generate JWT token
        //String jwt = jwtUtil.generateToken(user);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        String jwt = jwtUtil.generateToken(userDetails);

        logger.info("User {} logged in successfully. Generated JWT: {}", user.getEmail(), jwt);
        return ResponseEntity.ok(new LoginResponseDto(jwt, user.getUserType().name(),userMapper.toDto(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }

        logger.info("User {} logged out", request.getRemoteAddr());

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/current-user")
    public ResponseEntity<StudentDto> getCurrentUser() {
        Student student = studentService.getStudentById(authService.getCurrentUserId());
        StudentDto studentDto = studentMapper.toDto(student);
        return ResponseEntity.ok(studentDto);
    }
}

