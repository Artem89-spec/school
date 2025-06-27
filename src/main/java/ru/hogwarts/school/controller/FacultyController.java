package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("faculty")
public class FacultyController {
    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PostMapping("/params")
    public Faculty createFacultyWithParameters(@RequestParam String name, @RequestParam String color) {
        return facultyService.createFacultyWithParameters(name, color);
    }

    /**
     * когда объект не найден то ResponseEntity.notFound().build(); выдает код 404
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id) {
        Faculty foundFaculty = facultyService.findFaculties(id);
        if (foundFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @GetMapping("/all")
    public Collection<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    /**
     * когда ошибка вызвана неправильным запросом ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); выдает код 400
     *
     * @param faculty
     * @return
     */
    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty newFaculty = facultyService.editFaculty(faculty);
        if (newFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(newFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> removeFaculty(@PathVariable long id) {
        facultyService.removeFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("filter")
    public ResponseEntity<Collection<Faculty>> filteredFaculty(@RequestParam(required = false) String color) {
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.filteredFacultyByColor(color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }
}
