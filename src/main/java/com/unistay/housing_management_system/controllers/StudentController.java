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
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        logger.info("GET /api/students");
        return ResponseEntity.ok(studentService.getAllStudents());
    }


    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        logger.info("GET /api/students/{}", id);
        return ResponseEntity.ok(studentService.getStudentDtoById(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateStudent(@PathVariable Long id) {
        logger.info("PATCH /api/students/{}/deactivate", id);
        studentService.deactivateStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getStudentCount() {
        logger.info("GET /api/students/count");
        return ResponseEntity.ok(studentService.getStudentCount());
    }

    @GetMapping("/housed/count")
    public ResponseEntity<Long> getHousedStudentCount() {
        logger.info("GET /api/students/housed/count");
        return ResponseEntity.ok(studentService.getHousingStudentCount());
    }

    /*
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStudentStats() {
        logger.info("GET /api/students/stats");
        long total = studentService.getStudentCount();
        long housed = studentService.getHousingStudentCount();
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", total);
        stats.put("housedStudents", housed);
        return ResponseEntity.ok(stats);
    }*/
}