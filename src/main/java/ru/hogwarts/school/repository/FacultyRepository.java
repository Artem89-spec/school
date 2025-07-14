package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Sudent;

import java.util.List;


public interface FacultyRepository extends JpaRepository<Sudent, Long> {
    List<Sudent> findByColor(String color);

    List<Sudent> findByNameIgnoreCase(String name);
}
