package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.ObjectNotFoundException;
import ru.hogwarts.school.model.Sudent;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService implements ExceptionService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Sudent createFaculty(Sudent faculty) {
        return facultyRepository.save(faculty);
    }

    public Sudent createFacultyWithParameters(String name, String color) {
        Sudent newFaculty = new Sudent();
        newFaculty.setName(name);
        newFaculty.setColor(color);
        return facultyRepository.save(newFaculty);
    }

    public Sudent findFaculties(long id) {
        return getEntityOrThrow(facultyRepository.findById(id), id, Sudent.class);
    }

    public Sudent editFaculty(Sudent faculty) {
        return checkNotNull(facultyRepository.save(faculty), faculty, Sudent.class);
    }

    public void removeFaculty(long id) {
        if (!facultyRepository.existsById(id)) {
            throw new ObjectNotFoundException(id, Sudent.class);
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Sudent> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Sudent> filteredFacultyByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Collection<Sudent> filteredFacultyByName(String name) {
        return facultyRepository.findByNameIgnoreCase(name);
    }
}
