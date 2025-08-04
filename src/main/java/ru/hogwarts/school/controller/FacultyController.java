    package ru.hogwarts.school.controller;

    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import ru.hogwarts.school.model.Faculty;
    import ru.hogwarts.school.model.Student;
    import ru.hogwarts.school.service.FacultyService;

    import java.util.Collection;
    import java.util.Collections;

    @RestController
    @RequestMapping("faculty")
    public class FacultyController {
        private final FacultyService facultyService;

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
        public Faculty getFaculty(@PathVariable Long id) {
            return facultyService.findFaculties(id);
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
        public Faculty editFaculty(@RequestBody Faculty faculty) {
            return facultyService.editFaculty(faculty);
        }

        @DeleteMapping("{id}")
        public void removeFaculty(@PathVariable Long id) {
            facultyService.removeFaculty(id);
        }

        @GetMapping("filter")
        public ResponseEntity<Collection<Faculty>> filteredFaculty(
                @RequestParam(required = false) String color,
                @RequestParam(required = false) String facultyName) {
            if (color != null && !color.isBlank()) {
                return ResponseEntity.ok(facultyService.filteredFacultyByColor(color));
            }
            if (facultyName != null && !facultyName.isBlank()) {
                return ResponseEntity.ok(facultyService.filteredFacultyByName(facultyName));
            }
            return ResponseEntity.ok(Collections.emptyList());
        }

        @GetMapping("{facultyId}/students")
        public ResponseEntity<Collection<Student>> findStudentsByFaculty(@PathVariable Long facultyId) {
            Faculty faculty = facultyService.findFaculties(facultyId);
            return ResponseEntity.ok(faculty.getStudents());
        }

        @GetMapping("longest-faculty-name")
        public String findLongestFacultyName() {
            return facultyService.getLongestFacultyName();
        }
    }
