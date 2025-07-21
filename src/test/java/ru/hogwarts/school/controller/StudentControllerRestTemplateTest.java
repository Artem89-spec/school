package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerRestTemplateTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        facultyRepository.deleteAll();
        studentRepository.deleteAll();
    }

    private String getUrl(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    private Faculty createFaculty(String name, String color) {
        Faculty expectedFaculty = new Faculty();
        expectedFaculty.setName(name);
        expectedFaculty.setColor(color);
        return expectedFaculty;
    }

    private Faculty addFaculty(String name, String color) {
        return facultyRepository.save(createFaculty(name, color));
    }

    private Student createStudent(String name, int age) {
        Student expectedStudent = new Student();
        expectedStudent.setName(name);
        expectedStudent.setAge(age);
        return expectedStudent;
    }

    private Student addStudent(String name, int age) {
        return studentRepository.save(createStudent(name, age));
    }

    private Student createStudentWithFaculty(String name, int age, Faculty faculty) {
        Student expectedStudent = new Student();
        expectedStudent.setName(name);
        expectedStudent.setAge(age);
        expectedStudent.setFaculty(faculty);
        return expectedStudent;
    }

    private Student addStudentWithFaculty(String name, int age, Faculty  faculty) {
        return studentRepository.save(createStudentWithFaculty(name, age, faculty));
    }

    @Test
    @DisplayName("Создает корректного студента")
    void whenCreateStudent_ThenCreateCorrectStudent() throws Exception {
        Student expectedStudent = addStudent("nameStudent", 15);

        String url = getUrl("/student");

        ResponseEntity<Student> actualStudentResponse = restTemplate.postForEntity(url, expectedStudent, Student.class);
        assertNotNull(actualStudentResponse);
        assertEquals(HttpStatus.OK, actualStudentResponse.getStatusCode());

        Student actualStudent = actualStudentResponse.getBody();
        assertNotNull(actualStudent);
        assertEquals(actualStudent, expectedStudent);
    }

    @Test
    @DisplayName("Создает корректного студента с заданными параметрами")
    void createStudentWithParameters() throws Exception {
        Student expectedStudent = addStudent("nameStudent", 15);

        String url = getUrl("/student/params?name=" + expectedStudent.getName() + "&age=" + expectedStudent.getAge());

        ResponseEntity<Student> actualStudentResponse = restTemplate.postForEntity(url, null, Student.class);
        assertNotNull(actualStudentResponse);
        assertEquals(HttpStatus.OK, actualStudentResponse.getStatusCode());

        Student actualStudent = actualStudentResponse.getBody();
        assertNotNull(actualStudent);
        assertEquals(actualStudent.getName(), expectedStudent.getName());
        assertEquals(actualStudent.getAge(), expectedStudent.getAge());
    }

    @Test
    @DisplayName("Возвращает корректного студента")
    void whenGetStudent_ThenReturnCorrectStudent() throws Exception {
        Student expectedStudent = addStudent("nameStudent", 15);

        String url = getUrl("/student/%d".formatted(expectedStudent.getId()));

        ResponseEntity<Student> actualStudent = restTemplate.getForEntity(url, Student.class);
        assertNotNull(actualStudent);
        assertNotNull(actualStudent.getBody());
        assertEquals(actualStudent.getBody(), expectedStudent);
    }

    @Test
    @DisplayName("Возвращает всех студентов")
    void whenGetAllStudents_ThenReturnAllCorrectsStudents() throws Exception {
        Student studentOne = addStudent("nameStudentOne", 19);
        Student studentTwo = addStudent("nameStudentTwo", 20);
        List<Student> expectedStudents = Arrays.asList(studentOne, studentTwo);

        String url = getUrl("/student/all");

        ResponseEntity<Student[]> responseStudents = restTemplate.getForEntity(url, Student[].class);
        assertNotNull(responseStudents);
        assertEquals(HttpStatus.OK, responseStudents.getStatusCode());

        Student[] actualStudents = responseStudents.getBody();
        assertNotNull(actualStudents);

        List<Student> actualFacultiesList = Arrays.asList(actualStudents);
        assertNotNull(actualFacultiesList);
        assertEquals(actualFacultiesList.size(), expectedStudents.size());
        assertEquals(actualFacultiesList, expectedStudents);
    }

    @Test
    @DisplayName("Изменяет данные о студенте")
    void whenEditStudent_ThenReturnCorrectEditStudent() throws Exception {
        Student expectedStudent = addStudent("nameStudent", 15);

        Student existingStudent = studentRepository.findById(expectedStudent.getId()).orElseThrow();
        existingStudent.setName("newNameStudent");
        existingStudent.setAge(22);

        String url = getUrl("/student");

        ResponseEntity<Student> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(existingStudent),
                Student.class
        );
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Student updatedStudent = response.getBody();
        assertNotNull(updatedStudent);
        assertEquals(existingStudent.getId(), updatedStudent.getId());
        assertEquals("newNameStudent", updatedStudent.getName());
        assertEquals(22, updatedStudent.getAge());
    }

    @Test
    @DisplayName("Удаляет студента")
    void whenRemoveStudent_ThenStudentIsRemoved() throws Exception {
        Student expectedStudent = addStudent("nameStudent", 15);

        String url = getUrl("/student/%d".formatted(expectedStudent.getId()));

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertTrue(response.getStatusCode().is2xxSuccessful());

        ResponseEntity<Void> getResponse = restTemplate.getForEntity(url, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    @DisplayName("Находит всех студентов по конкретному возрасту или диапазону возрастов")
    void whenFilteredStudents_ThenStudentsIsFiltered() throws Exception {
        Student expectedStudent = addStudent("nameStudent", 15);

        String url = getUrl("/student/filter?name=" + expectedStudent.getName() + "&age=" + expectedStudent.getAge());

        ResponseEntity<Student[]> response = restTemplate.getForEntity(url, Student[].class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Student[] actualStudents = response.getBody();
        assertNotNull(actualStudents);
        assertTrue(actualStudents.length > 0);

        List<Student> actualStudentList = Arrays.asList(actualStudents);
        assertTrue(actualStudentList.stream()
                .allMatch(student ->
                        student.getAge() == expectedStudent.getAge()
                                && student.getName().equals(expectedStudent.getName())));

        assertEquals(1, actualStudents.length);
    }

    @Test
    @DisplayName("Находит факультет студента")
    void whenFindFacultyByStudent_ThenFacultyByStudentAreFound() throws Exception {
        Faculty expectedFaculty = addFaculty("nameFaculty", "color");
        Student student1 = addStudentWithFaculty("Bill", 20, expectedFaculty);
        Student student2 = addStudentWithFaculty("Bob", 15, expectedFaculty);

        studentRepository.saveAll(Arrays.asList(student1, student2));

        String url = getUrl("/student/%d/faculty".formatted(student1.getId()));

        ResponseEntity<Faculty> response = restTemplate.getForEntity(url, Faculty.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Faculty faculty = response.getBody();
        assertNotNull(faculty);
        assertNotNull(faculty.getStudents());
        assertEquals(expectedFaculty.getId(), faculty.getId());
    }
}