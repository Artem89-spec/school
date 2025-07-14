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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.hogwarts.school.model.Sudent;
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

    private Sudent expectedFaculty;
    private Sudent facultyOne;
    private Sudent facultyTwo;
    private List<Sudent> expectedFaculties;


    @BeforeEach
    void setUp() {
        facultyRepository.deleteAll();
        studentRepository.deleteAll();

        expectedFaculty = new Sudent();
        expectedFaculty.setName("nameFaculty");
        expectedFaculty.setColor("color");
        expectedFaculty = facultyRepository.save(expectedFaculty);

        facultyOne = new Sudent();
        facultyOne.setName("nameFacultyOne");
        facultyOne.setColor("colorOne");
        facultyOne = facultyRepository.save(facultyOne);
        facultyTwo = new Sudent();
        facultyTwo.setName("nameFacultyTwo");
        facultyTwo.setColor("colorTwo");
        facultyTwo = facultyRepository.save(facultyTwo);
        expectedFaculties = Arrays.asList(expectedFaculty, facultyOne, facultyTwo);
    }

    private String getUrl(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    @Test
    @DisplayName("Создает корректный факультет")
    void whenCreateFaculty_ThenCreateCorrectFaculty() throws Exception {
        String url = getUrl("/faculty");

        ResponseEntity<Sudent> actualFacultyResponse = restTemplate.postForEntity(url, expectedFaculty, Sudent.class);
        assertNotNull(actualFacultyResponse);
        assertEquals(HttpStatus.OK, actualFacultyResponse.getStatusCode());

        Sudent actualFaculty = actualFacultyResponse.getBody();
        assertNotNull(actualFaculty);
        assertEquals(actualFaculty, expectedFaculty);
    }


    @Test
    @DisplayName("Создает корректный факультет с заданными параметрами")
    void createFacultyWithParameters() throws Exception {
        String url = getUrl("/faculty/params?name=" + expectedFaculty.getName() + "&color=" + expectedFaculty.getColor());

        ResponseEntity<Sudent> actualFacultyResponse = restTemplate.postForEntity(url, null, Sudent.class);
        assertNotNull(actualFacultyResponse);
        assertEquals(HttpStatus.OK, actualFacultyResponse.getStatusCode());

        Sudent actualFaculty = actualFacultyResponse.getBody();
        assertNotNull(actualFaculty);
        assertEquals(actualFaculty.getName(), expectedFaculty.getName());
        assertEquals(actualFaculty.getColor(), expectedFaculty.getColor());
    }

    @Test
    @DisplayName("Возвращает корректный факультет")
    void whenGetFaculty_ThenReturnCorrectFaculty() throws Exception {
        String url = getUrl("/faculty/%d".formatted(expectedFaculty.getId()));

        ResponseEntity<Sudent> actualFaculty = restTemplate.getForEntity(url, Sudent.class);
        assertNotNull(actualFaculty);
        assertNotNull(actualFaculty.getBody());
        assertEquals(actualFaculty.getBody(), expectedFaculty);
    }

    @Test
    @DisplayName("Возвращает все факультеты")
    void whenGetAllFaculties_ThenReturnAllCorrectsFaculties() throws Exception {
        String url = getUrl("/faculty/all");

        ResponseEntity<Sudent[]> responseFaculties = restTemplate.getForEntity(url, Sudent[].class);
        assertNotNull(responseFaculties);
        assertEquals(HttpStatus.OK, responseFaculties.getStatusCode());

        Sudent[] actualFaculties = responseFaculties.getBody();
        assertNotNull(actualFaculties);

        List<Sudent> actualFacultiesList = Arrays.asList(actualFaculties);
        assertNotNull(actualFacultiesList);
        assertEquals(actualFacultiesList.size(), expectedFaculties.size());
        assertEquals(actualFacultiesList, expectedFaculties);
    }

    @Test
    @DisplayName("Изменяет данные о факультете")
    void whenEditFaculty_ThenReturnCorrectEditFaculty() throws Exception {
        Sudent existingFaculty = facultyRepository.findById(expectedFaculty.getId()).orElseThrow();
        existingFaculty.setName("newNameFaculty");
        existingFaculty.setColor("newColor");

        String url = getUrl("/faculty");

        ResponseEntity<Sudent> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(existingFaculty),
                Sudent.class
        );
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Sudent updatedFaculty = response.getBody();
        assertNotNull(updatedFaculty);
        assertEquals(existingFaculty.getId(), updatedFaculty.getId());
        assertEquals("newNameFaculty", updatedFaculty.getName());
        assertEquals("newColor", updatedFaculty.getColor());
    }

    @Test
    @DisplayName("Удаляет факультет")
    void whenRemoveFaculty_ThenFacultyIsRemoved() throws Exception {
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
        String url = getUrl("/faculty/filter?color=" + expectedFaculty.getColor() + "&name=" + expectedFaculty.getName());

        ResponseEntity<Sudent[]> response = restTemplate.getForEntity(url, Sudent[].class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Sudent[] actualFaculties = response.getBody();
        assertNotNull(actualFaculties);
        assertTrue(actualFaculties.length > 0);

        List<Sudent> actualFacultyList = Arrays.asList(actualFaculties);
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
        Sudent facultyTest = new Sudent();
        facultyTest.setName("testName");
        facultyTest.setColor("testColor");
        facultyTest = facultyRepository.save(facultyTest);

        Student student1 = new Student();
        student1.setName("Bill");
        student1.setAge(20);
        student1.setFaculty(facultyTest);

        Student student2 = new Student();
        student2.setName("Bob");
        student2.setAge(15);
        student2.setFaculty(facultyTest);

        studentRepository.saveAll(Arrays.asList(student1, student2));

        System.out.println("Faculty ID after save: " + facultyTest.getId());

        String url = getUrl("/faculty/%d/students".formatted(facultyTest.getId()));

        ResponseEntity<Student[]> response = restTemplate.getForEntity(url, Student[].class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Student[] students = response.getBody();
        assertNotNull(students);
        assertEquals(2, students.length);

        for (Student s : students) {
            assertNotNull(s.getFaculty());
            assertEquals(facultyTest.getId(), s.getFaculty().getId());
        }
    }
}