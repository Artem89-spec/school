package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.ObjectNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService implements ExceptionService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty createFacultyWithParameters(String name, String color) {
        Faculty newFaculty = new Faculty();
        newFaculty.setName(name);
        newFaculty.setColor(color);
        return facultyRepository.save(newFaculty);
    }

    public Faculty findFaculties(long id) {
        return getEntityOrThrow(facultyRepository.findById(id), id, Faculty.class);
    }

    public Faculty editFaculty(Faculty faculty) {
        return checkNotNull(facultyRepository.save(faculty), faculty, Faculty.class);
    }

    public void removeFaculty(long id) {
        if (!facultyRepository.existsById(id)) {
            throw new ObjectNotFoundException(id, Faculty.class);
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> filteredFacultyByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> filteredFacultyByName(String name) {
        return facultyRepository.findByNameIgnoreCase(name);
    }
}
