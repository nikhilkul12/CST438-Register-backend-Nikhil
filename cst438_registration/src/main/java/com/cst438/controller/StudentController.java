package com.cst438.controller;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin("http://localhost:3000")
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
    @PreAuthorize("hasRole('ADMIN')")
    public StudentDTO create(@RequestBody Student s, Principal principal) {
        String email = principal.getName();
        Student saved = sRepo.save(s);
        return new StudentDTO(saved.getStudent_id(), saved.getName(), saved.getEmail(), saved.getStatus());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentDTO> getById(@PathVariable Integer id, Principal principal) {
        String email = principal.getName();
        return sRepo.findById(id)
            .map(s -> ResponseEntity.ok(new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatus())))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentDTO> getAll(Principal principal) {
        String email = principal.getName();
        List<StudentDTO> dtos = new ArrayList<>();
        for (Student s : sRepo.findAll()) {
            dtos.add(new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatus()));
        }
        return dtos;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO> update(@PathVariable Integer id, @RequestBody Student updatedStudent, Principal principal) {
        String email = principal.getName();
        return sRepo.findById(id)
            .map(existingStudent -> {
                existingStudent.setName(updatedStudent.getName());
                existingStudent.setEmail(updatedStudent.getEmail());
                existingStudent.setStatus(updatedStudent.getStatus());
                Student savedStudent = sRepo.save(existingStudent);
                return ResponseEntity.ok(new StudentDTO(
                    savedStudent.getStudent_id(),
                    savedStudent.getName(),
                    savedStudent.getEmail(),
                    savedStudent.getStatus()
                ));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Integer id, @RequestParam(value = "FORCE", defaultValue = "false") Boolean force, Principal principal) {
        String email = principal.getName();
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

