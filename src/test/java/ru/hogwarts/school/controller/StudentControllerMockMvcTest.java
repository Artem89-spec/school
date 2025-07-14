package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class StudentControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
    }

    private Faculty createFaculty(String name, String color) {
        Faculty expectedFaculty = new Faculty();
        expectedFaculty.setName(name);
        expectedFaculty.setColor(color);
        return expectedFaculty;
    }

    private Student createStudent(String name, int age) {
        Student expectedStudent = new Student();
        expectedStudent.setName(name);
        expectedStudent.setAge(age);
        return expectedStudent;
    }

    @Test
    @DisplayName("Создает корректного студента")
    void whenCreateStudent_ThenCreateCorrectStudent() throws Exception {
        Student expectedStudent = createStudent("nameStudent", 15);

        String jsonContent = objectMapper.writeValueAsString(expectedStudent);

        when(studentRepository.save(any(Student.class))).thenReturn(expectedStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Создает корректного студента с заданными параметрами")
    void whenCreateStudentWithParameters_ThenCreateCorrectStudentWithParameters() throws Exception {
        Student expectedStudent = createStudent("nameStudent", 15);

        when(studentRepository.save(any(Student.class))).thenReturn(expectedStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/params")
                        .param("name", expectedStudent.getName())
                        .param("age", String.valueOf(expectedStudent.getAge())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedStudent.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(expectedStudent.getAge()));
    }

    @Test
    @DisplayName("Возвращает корректного студента")
    void whenGetStudent_ThenReturnCorrectStudent() throws Exception {
        Student expectedStudent = createStudent("nameStudent", 15);

        when(studentRepository.findById(expectedStudent.getId())).thenReturn(Optional.of(expectedStudent));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/%d".formatted(expectedStudent.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedStudent.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(expectedStudent.getAge()));
    }

    @Test
    @DisplayName("Возвращает всех студентов")
    void whenGetAllStudents_ThenReturnAllCorrectsStudents() throws Exception {
        Student studentOne = createStudent("nameStudentOne", 19);
        Student studentTwo = createStudent("nameStudentTwo", 20);
        List<Student> expectedStudents = Arrays.asList(studentOne, studentTwo);

        when(studentRepository.findAll()).thenReturn(expectedStudents);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(expectedStudents.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(studentOne.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(studentTwo.getName()));
    }

    @Test
    @DisplayName("Изменяет данные о студенте")
    void whenEditStudent_ThenReturnCorrectEditStudent() throws Exception {
        Student expectedStudent = createStudent("nameStudent", 15);
        expectedStudent.setName("newStudentName");
        expectedStudent.setAge(17);
        String jsonContent = objectMapper.writeValueAsString(expectedStudent);

        when(studentRepository.save(any(Student.class))).thenReturn(expectedStudent);

        mockMvc.perform(MockMvcRequestBuilders.put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedStudent.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(expectedStudent.getAge()));
    }

    @Test
    @DisplayName("Удаляет студента")
    void whenRemoveStudent_ThenStudentIsRemoved() throws Exception {
        Student expectedStudent = createStudent("nameStudent", 15);

        when(studentRepository.existsById(expectedStudent.getId())).thenReturn(true);
        doNothing().when(studentRepository).deleteById(expectedStudent.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/%d".formatted(expectedStudent.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Находит всех студентов по конкретному возрасту или диапазону возрастов")
    void whenFilteredStudents_ThenStudentsIsFiltered() throws Exception {
        Student studentOne = createStudent("nameStudentOne", 19);
        Student studentTwo = createStudent("nameStudentOne", 20);
        Student studentThree = createStudent("nameStudentOne", 21);
        List<Student> expectedStudents = Arrays.asList(studentOne, studentTwo, studentThree);

        String jsonArrayStudents = objectMapper.writeValueAsString(Arrays.asList(studentOne, studentTwo, studentThree));

        when(studentRepository.findByAge(studentTwo.getAge())).thenReturn(expectedStudents);
        when(studentRepository.findByAgeBetween(studentOne.getAge(), studentThree.getAge())).thenReturn(expectedStudents);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/filter")
                        .param("age", String.valueOf(studentTwo.getAge())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jsonArrayStudents));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/filter")
                .param("startAge", "19")
                .param("endAge", "21"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jsonArrayStudents));
    }

    @Test
    @DisplayName("Находит факультет студента")
    void whenFindFacultyByStudent_ThenFacultyByStudentAreFound() throws Exception {
        Faculty facultyTest = createFaculty("facultyName", "facultyColor");
        Student expectedStudent = createStudent("nameStudent", 15);
        Student studentOne = createStudent("nameStudentOne", 19);

        expectedStudent.setFaculty(facultyTest);
        studentOne.setFaculty(facultyTest);

       facultyTest.setStudents(Arrays.asList(expectedStudent, studentOne));

        String jsonFaculty = objectMapper.writeValueAsString(expectedStudent.getFaculty());

        when(studentRepository.findById(facultyTest.getId())).thenReturn(Optional.of(expectedStudent));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/%d/faculty".formatted(expectedStudent.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jsonFaculty));

        verify(studentRepository).findById(expectedStudent.getId());
    }
}