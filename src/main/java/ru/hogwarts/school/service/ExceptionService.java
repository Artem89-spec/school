package ru.hogwarts.school.service;

import ru.hogwarts.school.exception.ObjectNotFoundException;

import java.util.Optional;

public interface ExceptionService {
    default <T> T getEntityOrThrow(Optional<T> optional, Object id, Class<?> clazz) {
        return optional.orElseThrow(() -> new ObjectNotFoundException(id, clazz));
    }

    default <T> T checkNotNull(T entity, Object id, Class<?> clazz) {
        if (entity == null) {
            throw new ObjectNotFoundException(id, clazz);
        }
        return entity;
    }
}
