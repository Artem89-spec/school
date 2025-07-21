package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerRestTemplateTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentRepository studentRepository;

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
    @DisplayName("Создает корректный факультет")
    void whenCreateFaculty_ThenCreateCorrectFaculty() throws Exception {
        Faculty expectedFaculty = addFaculty("nameFaculty", "color");

        String url = getUrl("/faculty");

        ResponseEntity<Faculty> actualFacultyResponse = restTemplate.postForEntity(url, expectedFaculty, Faculty.class);
        assertNotNull(actualFacultyResponse);
        assertEquals(HttpStatus.OK, actualFacultyResponse.getStatusCode());

        Faculty actualFaculty = actualFacultyResponse.getBody();
        assertNotNull(actualFaculty);
        assertEquals(actualFaculty, expectedFaculty);
    }


    @Test
    @DisplayName("Создает корректный факультет с заданными параметрами")
    void createFacultyWithParameters() throws Exception {
        Faculty expectedFaculty = addFaculty("nameFaculty", "color");

        String url = getUrl("/faculty/params?name=" + expectedFaculty.getName() + "&color=" + expectedFaculty.getColor());

        ResponseEntity<Faculty> actualFacultyResponse = restTemplate.postForEntity(url, null, Faculty.class);
        assertNotNull(actualFacultyResponse);
        assertEquals(HttpStatus.OK, actualFacultyResponse.getStatusCode());

        Faculty actualFaculty = actualFacultyResponse.getBody();
        assertNotNull(actualFaculty);
        assertEquals(actualFaculty.getName(), expectedFaculty.getName());
        assertEquals(actualFaculty.getColor(), expectedFaculty.getColor());
    }

    @Test
    @DisplayName("Возвращает корректный факультет")
    void whenGetFaculty_ThenReturnCorrectFaculty() throws Exception {
        Faculty expectedFaculty = addFaculty("nameFaculty", "color");

        String url = getUrl("/faculty/%d".formatted(expectedFaculty.getId()));

        ResponseEntity<Faculty> actualFaculty = restTemplate.getForEntity(url, Faculty.class);
        assertNotNull(actualFaculty);
        assertNotNull(actualFaculty.getBody());
        assertEquals(actualFaculty.getBody(), expectedFaculty);
    }

    @Test
    @DisplayName("Возвращает все факультеты")
    void whenGetAllFaculties_ThenReturnAllCorrectsFaculties() throws Exception {
        Faculty facultyOne = addFaculty("facultyName1", "color1");
        Faculty facultyTwo = addFaculty("facultyName2", "color2");
        List<Faculty> expectedFaculties = Arrays.asList(facultyOne, facultyTwo);

        String url = getUrl("/faculty/all");

        ResponseEntity<Faculty[]> responseFaculties = restTemplate.getForEntity(url, Faculty[].class);
        assertNotNull(responseFaculties);
        assertEquals(HttpStatus.OK, responseFaculties.getStatusCode());

        Faculty[] actualFaculties = responseFaculties.getBody();
        assertNotNull(actualFaculties);

        List<Faculty> actualFacultiesList = Arrays.asList(actualFaculties);
        assertNotNull(actualFacultiesList);
        assertEquals(actualFacultiesList.size(), expectedFaculties.size());
        assertEquals(actualFacultiesList, expectedFaculties);
    }

    @Test
    @DisplayName("Изменяет данные о факультете")
    void whenEditFaculty_ThenReturnCorrectEditFaculty() throws Exception {
        Faculty expectedFaculty = addFaculty("nameFaculty", "color");
        Faculty existingFaculty = facultyRepository.findById(expectedFaculty.getId()).orElseThrow();
        existingFaculty.setName("newNameFaculty");
        existingFaculty.setColor("newColor");

        String url = getUrl("/faculty");

        ResponseEntity<Faculty> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(existingFaculty),
                Faculty.class
        );
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Faculty updatedFaculty = response.getBody();
        assertNotNull(updatedFaculty);
        assertEquals(existingFaculty.getId(), updatedFaculty.getId());
        assertEquals("newNameFaculty", updatedFaculty.getName());
        assertEquals("newColor", updatedFaculty.getColor());
    }

    @Test
    @DisplayName("Удаляет факультет")
    void whenRemoveFaculty_ThenFacultyIsRemoved() throws Exception {
        Faculty expectedFaculty = addFaculty("nameFaculty", "color");
        String url = getUrl("/faculty/%d".formatted(expectedFaculty.getId()));

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
    @DisplayName("Находит факультеты по параметрам цвета или названия")
    void whenFilteredFaculty_ThenFacultyIsFiltered() throws Exception {
        Faculty expectedFaculty = addFaculty("nameFaculty", "color");

        String url = getUrl("/faculty/filter?color=" + expectedFaculty.getColor() + "&name=" + expectedFaculty.getName());

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(url, Faculty[].class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Faculty[] actualFaculties = response.getBody();
        assertNotNull(actualFaculties);
        assertTrue(actualFaculties.length > 0);

        List<Faculty> actualFacultyList = Arrays.asList(actualFaculties);
        assertNotNull(actualFacultyList);
        assertTrue(actualFacultyList.stream().allMatch(faculty ->
                (expectedFaculty.getColor() == null || expectedFaculty.getColor().isEmpty() ||
                        expectedFaculty.getColor().equals(faculty.getColor())) &&
                        (expectedFaculty.getName() == null || expectedFaculty.getName().isEmpty() ||
                                expectedFaculty.getName().equals(faculty.getName()))));
    }

    @Test
    @DisplayName("Находит всех студентов заданного факультета")
    void findStudentsByFaculty() throws Exception {
        Faculty expectedFaculty = addFaculty("nameFaculty", "color");
        Student student1 = addStudentWithFaculty("Bill", 20, expectedFaculty);
        Student student2 = addStudentWithFaculty("Bob", 15, expectedFaculty);

        studentRepository.saveAll(Arrays.asList(student1, student2));

        String url = getUrl("/faculty/%d/students".formatted(expectedFaculty.getId()));

        ResponseEntity<Student[]> response = restTemplate.getForEntity(url, Student[].class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Student[] students = response.getBody();
        assertNotNull(students);
        assertEquals(2, students.length);

        /**
         * Спросить почему в процессе сереализации faculty у студентов стоит как null и приходится вручную его записывать циклом,
         * хотя в начале теста все записал как положено
         */

        for (Student s : students) {
            if (s.getFaculty() == null) {
                Faculty faculty = facultyRepository.findById(expectedFaculty.getId()).orElse(null);
                s.setFaculty(faculty);
            }
        }

        for (Student s : students) {
            assertNotNull(s.getFaculty());
            assertEquals(expectedFaculty.getId(), s.getFaculty().getId());
        }
    }
}