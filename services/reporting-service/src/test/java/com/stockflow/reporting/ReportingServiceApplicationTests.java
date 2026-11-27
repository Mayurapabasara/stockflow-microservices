package com.stockflow.reporting;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest(properties="spring.cloud.config.enabled=false")
@ActiveProfiles("test")
class ReportingServiceApplicationTests {
    @Test void contextLoads(){}
}
