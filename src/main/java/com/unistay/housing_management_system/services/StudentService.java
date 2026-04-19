package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.StudentRepository;
import com.unistay.housing_management_system.dtos.response.StudentDto;
import com.unistay.housing_management_system.entity.Student;
import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import com.unistay.housing_management_system.mapping.StudentMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentDto getStudentDtoById(Long id) {
        logger.info("Fetching student with id: {}", id);

        Student student = getStudentById(id);
        return studentMapper.toDto(student);
    }

    public Student getStudentByUniversityId(String universityId) {
        logger.info("Fetching student with universityId: {}", universityId);

        return studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> {
                    logger.warn("Student not found with universityId: {}", universityId);
                    return new ResourceNotFoundException(
                            "Student not found with universityId: " + universityId);
                });
    }

    public List<StudentDto> getAllStudents() {
        logger.info("Fetching all students");

        List<Student> students = studentRepository.findAll();
        logger.info("Total students found: {}", students.size());

        return studentMapper.toDtoList(students);
    }

    @Transactional
    public void deactivateStudent(Long id) {
        logger.info("Deactivating student with id: {}", id);

        Student student = getStudentById(id);

        if (Boolean.FALSE.equals(student.getIsActive())) {
            logger.warn("Student [{}] is already inactive", id);
            throw new IllegalStateException("Student is already inactive.");
        }

        student.setIsActive(false);
        studentRepository.save(student);
        logger.info("Student [{}] deactivated successfully", id);
    }

    // Helper methods
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Student not found with id: {}", id);
                    return new ResourceNotFoundException("Student not found with id: " + id);
                });
    }

}
