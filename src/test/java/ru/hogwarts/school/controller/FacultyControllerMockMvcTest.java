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
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class FacultyControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyRepository facultyRepository;

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
    @DisplayName("Создает корректный факультет")
    void whenCreateFaculty_ThenCreateCorrectFaculty() throws Exception {
        Faculty expectedFaculty = createFaculty("nameFaculty", "color");
        String jsonContent = objectMapper.writeValueAsString(expectedFaculty);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(expectedFaculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Создает корректный факультет с заданными параметрами")
    void whenCreateFacultyWithParameters_ThenCreateCorrectFacultyWithParameters() throws Exception {
        Faculty expectedFaculty = createFaculty("nameFaculty", "color");

        when(facultyRepository.save(any(Faculty.class))).thenReturn(expectedFaculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty/params")
                        .param("name", expectedFaculty.getName())
                        .param("color", expectedFaculty.getColor()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedFaculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(expectedFaculty.getColor()));
    }

    @Test
    @DisplayName("Возвращает корректный факультет")
    void whenGetFaculty_ThenReturnCorrectFaculty() throws Exception {
        Faculty expectedFaculty = createFaculty("nameFaculty", "color");

        when(facultyRepository.findById(expectedFaculty.getId())).thenReturn(Optional.of(expectedFaculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/%d".formatted(expectedFaculty.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedFaculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(expectedFaculty.getColor()));
    }

    @Test
    @DisplayName("Возвращает все факультеты")
    void whenGetAllFaculties_ThenReturnAllCorrectsFaculties() throws Exception {
        Faculty facultyOne = createFaculty("facultyName1", "color1");
        Faculty facultyTwo = createFaculty("facultyName2", "color2");
        List<Faculty> expectedFaculties = Arrays.asList(facultyOne, facultyTwo);

        when(facultyRepository.findAll()).thenReturn(expectedFaculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(expectedFaculties.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(facultyOne.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(facultyTwo.getName()));
    }

    @Test
    @DisplayName("Изменяет данные о факультете")
    void whenEditFaculty_ThenReturnCorrectEditFaculty() throws Exception {
        Faculty expectedFaculty = createFaculty("nameFaculty", "color");
        expectedFaculty.setName("newFacultyName");
        expectedFaculty.setColor("newColor");

        String jsonContent = objectMapper.writeValueAsString(expectedFaculty);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(expectedFaculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedFaculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(expectedFaculty.getColor()));
    }

    @Test
    @DisplayName("Удаляет факультет")
    void whenRemoveFaculty_ThenFacultyIsRemoved() throws Exception {
        Faculty expectedFaculty = createFaculty("nameFaculty", "color");

        when(facultyRepository.existsById(expectedFaculty.getId())).thenReturn(true);
        doNothing().when(facultyRepository).deleteById(expectedFaculty.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/%d".formatted(expectedFaculty.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Находит факультеты по параметрам цвета или названия")
    void whenFilteredFaculty_ThenFacultyIsFiltered() throws Exception {
        Faculty facultyOne = createFaculty("facultyNameOne", "colorOne");
        Faculty facultyTwo = createFaculty("facultyNameTwo", "colorTwo");
        List<Faculty> expectedFaculties = Arrays.asList(facultyOne, facultyTwo);

        String jsonArrayFaculties = objectMapper.writeValueAsString(Arrays.asList(facultyOne, facultyTwo));

        when(facultyRepository.findByColor("colorOne")).thenReturn(expectedFaculties);
        when(facultyRepository.findByNameIgnoreCase("facultyNameTwo")).thenReturn(expectedFaculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/filter")
                        .param("color", "colorOne"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jsonArrayFaculties));

        mockMvc.perform((MockMvcRequestBuilders.get("/faculty/filter"))
                        .param("facultyName", "facultyNameTwo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jsonArrayFaculties));

        mockMvc.perform((MockMvcRequestBuilders.get("/faculty/filter"))
                        .param("facultyName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        mockMvc.perform((MockMvcRequestBuilders.get("/faculty/filter"))
                        .param("color", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/filter"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        verify(facultyRepository).findByColor("colorOne");
        verify(facultyRepository).findByNameIgnoreCase("facultyNameTwo");
    }

    @Test
    @DisplayName("Находит всех студентов заданного факультета")
    void whenFindStudentsByFaculty_ThenStudentsByFacultyAreFound() throws Exception {
        Faculty expectedFaculty = createFaculty("nameFaculty", "color");
        Student student1 = new Student(1, "Bill", 20);
        Student student2 = new Student(2, "Bob", 15);

        expectedFaculty.setStudents(Arrays.asList(student1, student2));

        student1.setFaculty(expectedFaculty);
        student2.setFaculty(expectedFaculty);

        String jsonStudents = objectMapper.writeValueAsString(expectedFaculty.getStudents());

        when(facultyRepository.findById(expectedFaculty.getId())).thenReturn(Optional.of(expectedFaculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/%d/students".formatted(expectedFaculty.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(jsonStudents));

        verify(facultyRepository).findById(expectedFaculty.getId());
    }
}