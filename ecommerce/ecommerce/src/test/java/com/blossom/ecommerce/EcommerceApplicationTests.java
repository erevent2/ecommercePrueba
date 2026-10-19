package com.blossom.ecommerce;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringApplication;

@SpringBootTest
class EcommerceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainDelegatesToSpringApplication() {
		try (MockedStatic<SpringApplication> springApplication = org.mockito.Mockito.mockStatic(SpringApplication.class)) {
			EcommerceApplication.main(new String[]{"--spring.main.web-application-type=none"});

			springApplication.verify(() -> SpringApplication.run(
					EcommerceApplication.class, new String[]{"--spring.main.web-application-type=none"}));
		}
	}
}
