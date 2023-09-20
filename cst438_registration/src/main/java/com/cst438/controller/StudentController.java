package com.cst438.controller;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository sRepo;
    private final EnrollmentRepository eRepo;

    public StudentController(StudentRepository sRepo, EnrollmentRepository eRepo) {
        this.sRepo = sRepo;
        this.eRepo = eRepo;
    }

    @PostMapping
    public StudentDTO create(@RequestBody Student s) {
        Student saved = sRepo.save(s);
        return new StudentDTO(saved.getStudent_id(), saved.getName(), saved.getEmail(), saved.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Integer id) {
        return sRepo.findById(id)
            .map(s -> ResponseEntity.ok(new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatus())))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<StudentDTO> getAll() {
        List<StudentDTO> dtos = new ArrayList<>();
        for (Student s : sRepo.findAll()) {
            dtos.add(new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatus()));
        }
        return dtos;
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Integer id, @RequestBody Student s) {
        if (sRepo.existsById(id)) {
            Student updated = sRepo.save(s);
            return ResponseEntity.ok(new StudentDTO(updated.getStudent_id(), updated.getName(), updated.getEmail(), updated.getStatus()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @RequestParam(value = "FORCE", defaultValue = "false") Boolean force) {
        return sRepo.findById(id).map(s -> {
            List<Enrollment> enrolls = eRepo.findStudentSchedule(s.getEmail(), 2023, "Spring");
            
            if (!enrolls.isEmpty() && !force) {
                return ResponseEntity.status(409).body("Cannot delete student as there are enrollments. Use FORCE=true to override.");
            }
            
            sRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

