package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.*;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long currentId = 1L;

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(currentId);
        faculties.put(currentId, faculty);
        currentId++;
        return faculty;
    }

    public Faculty createFacultyWithParameters(String name, String color) {
        Faculty newFaculty = new Faculty(currentId, name, color);
        faculties.put(currentId, newFaculty);
        currentId++;
        return newFaculty;
    }

    public Faculty getFaculties(long id) {
        return faculties.get(id);
    }

    public Collection<Faculty> getAllFaculties() {
        return Collections.unmodifiableCollection(faculties.values());
    }

    public Faculty editFaculty(Faculty faculty) {
        if (!faculties.containsKey(faculty.getId())) {
            return null;
        }
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty removeFaculty(long id) {
        return faculties.remove(id);
    }

    public Collection<Faculty> filteredFacultyByColor(String color) {
        List<Faculty> results = new ArrayList<>();
        for (Faculty faculty : faculties.values()) {
            if (faculty.getColor().equals(color)) {
                results.add(faculty);
            }
        }
        return results;
    }
}
