package com.stockflow.notification;
import com.stockflow.notification.service.impl.NotificationDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@ActiveProfiles("test")
class NotificationServiceApplicationTests {
    @Autowired ApplicationContext context;
    @Test void startsWithoutEmailDispatchWhenMailIsDisabled() {
        assertThat(context.getBeansOfType(NotificationDispatcher.class)).isEmpty();
    }
}
