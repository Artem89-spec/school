package ru.hogwarts.school.exception;

public class NoStudentsNotFoundException extends RuntimeException {
  public NoStudentsNotFoundException() {
    super("No students not found in database");
  }
}
