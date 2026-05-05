package com.cs;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基类
 *
 * @author LivePulse
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ConnGoogleAnalyticOpenApp.class)
@ActiveProfiles("test")
@Transactional
public abstract class BaseTest {

    // 测试基类，提供通用测试配置
}