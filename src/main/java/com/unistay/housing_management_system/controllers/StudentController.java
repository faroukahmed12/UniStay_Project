package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.response.StudentDto;
import com.unistay.housing_management_system.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        logger.info("GET /api/students");
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        logger.info("GET /api/students/{}", id);
        return ResponseEntity.ok(studentService.getStudentDtoById(id));
    }

    @PatchMapping("/{id}/deactivate")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateStudent(@PathVariable Long id) {
        logger.info("PATCH /api/students/{}/deactivate", id);
        studentService.deactivateStudent(id);
        return ResponseEntity.noContent().build();
    }
}