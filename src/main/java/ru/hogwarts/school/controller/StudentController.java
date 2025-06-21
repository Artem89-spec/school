package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;

    @Autowired
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
    public ResponseEntity<Student> getStudent(@PathVariable long id) {
        Student foundStudent = studentService.getStudent(id);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @GetMapping("/all")
    public Collection<Student> getAllFaculties() {
        return studentService.getAllStudents();
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student newStudent = studentService.editStudent(student);
        if (newStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(newStudent);
    }

    @DeleteMapping("{id}")
    public Student removeStudent(@PathVariable long id) {
        return studentService.removeStudent(id);
    }

    @GetMapping("filter")
    public ResponseEntity<Collection<Student>> filteredStudents(@RequestParam(required = false) int age) {
        if (age > 0) {
           return    ResponseEntity.ok(studentService.filteredStudentByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }
}
