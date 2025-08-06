package ru.hogwarts.school.exception;

public class NoFacultyNameException extends RuntimeException {
    public NoFacultyNameException() {
        super("No faculty name not found in database");
    }
}
