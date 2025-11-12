package com.cy.gateway;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("依赖外部注册中心与配置中心，默认跳过")
class GatewayApplicationTests {

    @Test
    void contextLoads() {
    }

}
