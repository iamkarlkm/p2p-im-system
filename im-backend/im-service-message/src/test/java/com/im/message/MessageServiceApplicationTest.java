package com.im.message;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 应用上下文加载测试
 * 验证Spring Boot应用能正常启动
 */
@SpringBootTest
@ActiveProfiles("test")
class MessageServiceApplicationTest {

    @Test
    void contextLoads() {
        // 验证应用上下文能正常加载
    }
}
