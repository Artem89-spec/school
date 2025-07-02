package ru.hogwarts.school.exception;

public class ObjectNotFoundException extends RuntimeException {
    private final Object id;

    public ObjectNotFoundException(Object id, Class<?> clazz) {
        super("[%s] not found by id: [%s]".formatted(clazz.getSimpleName(), id));
        this.id = id;
    }

    public Object getId() {
        return id;
    }
}
