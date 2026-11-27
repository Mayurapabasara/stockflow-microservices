package com.stockflow.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.profiles.active=native",
		"spring.cloud.config.server.native.search-locations=classpath:/"
})
class ConfigServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
