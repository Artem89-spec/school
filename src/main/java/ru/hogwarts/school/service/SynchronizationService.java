package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SynchronizationService {
    Object flag = new Object();

    Logger logger = LoggerFactory.getLogger(SynchronizationService.class);

    default void printSynchronized(String studentName, String threadName) {
        synchronized (flag)  {
            logger.info("{}: {}", threadName, studentName);
        }
    }
}
