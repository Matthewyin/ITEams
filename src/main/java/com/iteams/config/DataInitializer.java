package com.iteams.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 数据初始化器
 * <p>
 * 用于在应用程序启动时自动执行SQL脚本
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DataSource dataSource;

    /**
     * 初始化数据库
     */
    @PostConstruct
    public void initializeDatabase() {
        try {
            log.info("开始初始化用户数据...");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/init-user.sql"));
            populator.execute(dataSource);
            log.info("用户数据初始化完成");
        } catch (Exception e) {
            log.error("初始化用户数据时出错", e);
        }
    }
}
