package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import ru.hogwarts.school.model.Sudent;
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
    private TestRestTemplate restTemplate = new TestRestTemplate();
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;

    private Student expectedStudent;
    private Student studentOne;
    private Student studentTwo;
    private List<Student> expectedStudents;

    @BeforeEach
    void setUp() {
        facultyRepository.deleteAll();
        studentRepository.deleteAll();

        expectedStudent = new Student();
        expectedStudent.setName("nameStudent");
        expectedStudent.setAge(15);
        expectedStudent = studentRepository.save(expectedStudent);

        studentOne = new Student();
        studentOne.setName("nameStudentOne");
        studentOne.setAge(19);
        studentOne = studentRepository.save(studentOne);
        studentTwo = new Student();
        studentTwo.setName("nameStudentTwo");
        studentTwo.setAge(20);
        studentTwo = studentRepository.save(studentTwo);
        expectedStudents = Arrays.asList(expectedStudent, studentOne, studentTwo);
    }

    private String getUrl(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    @Test
    @DisplayName("Создает корректного студента")
    void whenCreateStudent_ThenCreateCorrectStudent() throws Exception {
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
        String url = getUrl("/student/%d".formatted(expectedStudent.getId()));

        ResponseEntity<Student> actualStudent = restTemplate.getForEntity(url, Student.class);
        assertNotNull(actualStudent);
        assertNotNull(actualStudent.getBody());
        assertEquals(actualStudent.getBody(), expectedStudent);
    }

    @Test
    @DisplayName("Возвращает всех студентов")
    void whenGetAllStudents_ThenReturnAllCorrectsStudents() throws Exception {
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
        String url = getUrl("/student/filter?name=" + expectedStudent.getName() + "&age=" + expectedStudent.getAge());

        ResponseEntity<Student[]> response = restTemplate.getForEntity(url, Student[].class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Student[] actualStudents = response.getBody();
        assertNotNull(actualStudents);
        assertTrue(actualStudents.length > 0);

        List<Student> actualStudentList = Arrays.asList(actualStudents);
        assertNotNull(actualStudentList);
        assertTrue(actualStudentList.stream().allMatch(student ->
                (expectedStudent.getAge() == 0 ||
                        expectedStudent.getAge() == (student.getAge())) &&
                        (expectedStudent.getName() == null || expectedStudent.getName().isEmpty() ||
                                expectedStudent.getName().equals(student.getName()))));
    }

    @Test
    @DisplayName("Находит факультет студента")
    void whenFindFacultyByStudent_ThenFacultyByStudentAreFound() throws Exception {
        Sudent facultyTest = new Sudent();
        facultyTest.setName("testName");
        facultyTest.setColor("testColor");
        facultyTest = facultyRepository.save(facultyTest);

        Student student1 = new Student();
        student1.setName("Poll");
        student1.setAge(23);
        student1.setFaculty(facultyTest);

        Student student2 = new Student();
        student2.setName("Fill");
        student2.setAge(21);
        student2.setFaculty(facultyTest);

        studentRepository.saveAll(Arrays.asList(student1, student2));

        System.out.println("Faculty ID after save: " + facultyTest.getId());

        String url = getUrl("/student/%d/faculty".formatted(student1.getId()));

        ResponseEntity<Sudent> response = restTemplate.getForEntity(url, Sudent.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Sudent faculty = response.getBody();
        assertNotNull(faculty);
        assertNotNull(faculty.getStudents());
        assertEquals(facultyTest.getId(), faculty.getId());
    }
}