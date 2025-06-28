package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PostMapping("/params")
    public Student createStudentWithParameters(@RequestParam String name, @RequestParam int age) {
        return studentService.createStudentWithParameters(name, age);
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable long id) {
        return  studentService.findStudent(id);
    }

    @GetMapping("/all")
    public Collection<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PutMapping
    public Student editStudent(@RequestBody Student student) {
        return studentService.editStudent(student);
    }

    @DeleteMapping("{id}")
    public void removeStudent(@PathVariable long id) {
        studentService.removeStudent(id);
    }

    @GetMapping("filter")
    public ResponseEntity<Collection<Student>> filteredStudents(
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer startAge,
            @RequestParam (required = false) Integer endAge) {
        if (age != null && age > 0) {
            return ResponseEntity.ok(studentService.filteredStudentByAge(age));
        }
        if (startAge !=  null && endAge != null && startAge > 0 && endAge > 0 && endAge >= startAge) {
            return ResponseEntity.ok(studentService.findByAgeBetween(startAge, endAge));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("{studentId}/faculty")
    public ResponseEntity<Faculty> findFacultyByStudent(@PathVariable Long studentId) {
        Student student = studentService.findStudent(studentId);
        return ResponseEntity.ok(student.getFaculty());
    }
}
