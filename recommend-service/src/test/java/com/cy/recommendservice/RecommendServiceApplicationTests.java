package com.cy.recommendservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("需依赖 MQ、Nacos 等外部组件，默认跳过")
class RecommendServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
