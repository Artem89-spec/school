package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.ObjectNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService implements ExceptionService {
    private final FacultyRepository facultyRepository;

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("A {} was created", faculty);
        return facultyRepository.save(faculty);
    }

    public Faculty createFacultyWithParameters(String name, String color) {
        logger.info("Was invoked method for create faculty with parameters");
        Faculty newFaculty = new Faculty();
        newFaculty.setName(name);
        newFaculty.setColor(color);
        logger.debug("A {} was created with this {} and {}", newFaculty, name, color);
        return facultyRepository.save(newFaculty);
    }

    public Faculty findFaculties(long id) {
        logger.info("Method findFaculties with iD {} invoked", id);
        Optional<Faculty> optionalFaculty = facultyRepository.findById(id);
        if (optionalFaculty.isPresent()) {
            logger.debug("Found faculty: {}", optionalFaculty.get());
        } else {
            logger.warn("Faculty with id {} not found", id);
        }
        return getEntityOrThrow(optionalFaculty, id, Faculty.class);
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Method editFaculty with iD {} invoked", faculty.getId());
        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.debug("Faculty after save: {}", savedFaculty);
        return checkNotNull(savedFaculty, faculty, Faculty.class);
    }

    public void removeFaculty(long id) {
        logger.info("Method removeFaculty with iD {} invoked", id);
        if (!facultyRepository.existsById(id)) {
            logger.error("Faculty with id {} does not exist", id);
            throw new ObjectNotFoundException(id, Faculty.class);
        }
        logger.debug("Faculty with ID {} has been removed", id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        logger.info("Method getAllFaculties invoked");
        Collection<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            logger.warn("No faculties found");
        }
        logger.debug("Number of faculties found: {}", faculties.size());
        return faculties;
    }

    public Collection<Faculty> filteredFacultyByColor(String color) {
        logger.info("Method filteredFacultyByColor with color {} invoked", color);
        Collection<Faculty> faculties = facultyRepository.findByColor(color);
        if (faculties.isEmpty()) {
            logger.warn("No faculty found with color {}", color);
        }
        logger.debug("Faculty with color {} found: {}", color, faculties);
        return faculties;
    }

    public Collection<Faculty> filteredFacultyByName(String name) {
        logger.info("Method filteredFacultyByName with name {} invoked", name);
        Collection<Faculty> faculties = facultyRepository.findByNameIgnoreCase(name);
        if (faculties.isEmpty()) {
            logger.warn("No faculty found with name {}", name);
        }
        logger.debug("Faculty with name {} found: {}", name, faculties);
        return faculties;
    }
}
