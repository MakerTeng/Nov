package com.cy.videoservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("集成测试依赖 Nacos、MySQL 等外部环境，默认跳过")
class VideoServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
