package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NoStudentsNotFoundException;
import ru.hogwarts.school.exception.ObjectNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;

@Service
public class StudentService implements ExceptionService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student createStudentWithParameters(String name, int age) {
        Student newStudent = new Student();
        newStudent.setName(name);
        newStudent.setAge(age);
        return studentRepository.save(newStudent);
    }

    public Student findStudent(long id) {
        return getEntityOrThrow(studentRepository.findById(id), id, Student.class);
    }

    public Student editStudent(Student student) {
        Student savedStudent = studentRepository.save(student);
        return checkNotNull(savedStudent, student.getId(), Student.class);
    }

    public void removeStudent(long id) {
        if (!studentRepository.existsById(id)) {
            throw new ObjectNotFoundException(id, Student.class);
        }
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> filteredStudentByAge(int age) {
      return studentRepository.findByAge(age);
    }

    public Collection<Student> findByAgeBetween(int startAge, int endAge) {
        return studentRepository.findByAgeBetween(startAge, endAge);
    }

    public int getStudentsCount() {
        return studentRepository.countAllStudents();
    }

    public double getAverageAgeOfStudents() {
        Double avg = studentRepository.averageAgeOfStudents();
        if (avg == null) {
            throw new NoStudentsNotFoundException();
        }
        return avg;
    }

    public List<Student> getLastFiveStudents()  {
       return studentRepository.findLastFiveStudents();
    }
}
