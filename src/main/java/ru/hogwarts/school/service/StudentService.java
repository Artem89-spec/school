package ru.hogwarts.school.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.*;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private long currentId = 1L;

    public Student createStudent(Student student) {
        student.setId(currentId);
        students.put(currentId, student);
        currentId++;
        return student;
    }

    public Student createStudentWithParameters(String name, int age) {
        Student newStudent = new Student(currentId, name, age);
        students.put(currentId, newStudent);
        currentId++;
        return newStudent;
    }

    public Student getStudent(long id) {
        return students.get(id);
    }

    public Collection<Student> getAllStudents() {
        return Collections.unmodifiableCollection(students.values());
    }

    public Student editStudent(Student student) {
        if (!students.containsKey(student.getId())) {
            return null;
        }
        students.put(student.getId(), student);
        return student;
    }

    public Student removeStudent(long id) {
        return students.remove(id);
    }

    public Collection<Student> filteredStudentByAge(int age) {
        List<Student> results = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getAge() == age) {
                results.add(student);
            }
        }
        return results;
    }
}
