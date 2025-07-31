package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("A {} was created", student);
        return studentRepository.save(student);

    }

    public Student createStudentWithParameters(String name, int age) {
        logger.info("Was invoked method for create student with parameters");
        Student newStudent = new Student();
        newStudent.setName(name);
        newStudent.setAge(age);
        logger.debug("A {} was created with this {} and {}", newStudent, name, age);
        return studentRepository.save(newStudent);
    }

    public Student findStudent(long id) {
        logger.info("Method findStudent with iD {} invoked", id);
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            logger.debug("Found student: {}", optionalStudent.get());
        } else {
            logger.warn("Student with id {} not found", id);
        }
        return getEntityOrThrow(optionalStudent, id, Student.class);
    }

    public Student editStudent(Student student) {
        logger.info("Method editStudent with iD {} invoked", student.getId());
        Student savedStudent = studentRepository.save(student);
        logger.debug("Student after save: {}", savedStudent);
        return checkNotNull(savedStudent, student.getId(), Student.class);
    }

    public void removeStudent(long id) {
        logger.info("Method removeStudent with iD {} invoked",  id);
        if (!studentRepository.existsById(id)) {
            logger.error("Student with id {} does not exist", id);
            throw new ObjectNotFoundException(id, Student.class);
        }
        logger.debug("Student with ID {} has been removed", id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        logger.info("Method getAllStudents invoked");
        Collection<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            logger.warn("No students found");
        }
        logger.debug("Number of students found: {}", students.size());
        return students;
    }

    public Collection<Student> filteredStudentByAge(int age) {
        logger.info("Method filteredStudentByAge with age {} invoked", age);
        Collection<Student> students = studentRepository.findByAge(age);
        if (students.isEmpty()) {
            logger.warn("No students found with age {}", age);
        }
        logger.debug("Number of students with age {} found: {}", age, students.size());
        return students;
    }

    public Collection<Student> findByAgeBetween(int startAge, int endAge) {
        logger.info("Method findByAgeBetween age {} and age {} invoked", startAge, endAge);
        Collection<Student> students = studentRepository.findByAgeBetween(startAge, endAge);
        if (students.isEmpty()) {
            logger.warn("No students found between ages {} and {}", startAge, endAge);
        }
        logger.debug("Number of students between ages {} and {} found: {}", startAge, endAge, students.size());
        return students;
    }

    public int getStudentsCount() {
        logger.info("Method getStudentsCount invoked");
        return studentRepository.countAllStudents();
    }

    public double getAverageAgeOfStudents() {
        logger.info("Method getAverageAgeOfStudents invoked");
        Double avg = studentRepository.averageAgeOfStudents();
        if (avg == null) {
            logger.error("No students found to calculate average age");
            throw new NoStudentsNotFoundException();
        }
        logger.debug("Average age of students: {}", avg);
        return avg;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Method getLastFiveStudents invoked");
        List<Student> students = studentRepository.findLastFiveStudents();
        if (students.isEmpty()) {
            logger.warn("No students found in last five");
        }
        logger.debug("Number of last five students: {}", students.size());
        return students;
    }
}
