package ru.hogwarts.school.service;

import ru.hogwarts.school.exception.InvalidSymbolException;
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

    default void validateSymbol(String symbol) {
        if (symbol == null) {
            throw new InvalidSymbolException("Символ не может быть null");
        }

        if (symbol.length() > 1) {
            throw new InvalidSymbolException("Должен быть передан один символ");
        }

        if (!Character.isLetter(symbol.charAt(0))) {
            throw new InvalidSymbolException("Символ должен быть буквой");
        }
    }
}
