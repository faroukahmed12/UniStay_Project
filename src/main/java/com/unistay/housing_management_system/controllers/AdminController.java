package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.response.AdminDto;
import com.unistay.housing_management_system.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        logger.info("GET /api/admins");
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable Long id) {
        logger.info("GET /api/admins/{}", id);
        return ResponseEntity.ok(adminService.getAdminDtoById(id));
    }
}
