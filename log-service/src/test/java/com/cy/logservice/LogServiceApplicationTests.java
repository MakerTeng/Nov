package com.cy.logservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("依赖 RabbitMQ 与数据库，默认跳过")
class LogServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
