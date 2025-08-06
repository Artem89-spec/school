package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"server.port=8080"})
class SchoolApplicationTests {

	@Test
	void contextLoads() {
		// TODO Этот тест предназначен для проверки запуска контекста приложения
	}

}
