package com.iteams.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化数据配置属性
 * <p>
 * 用于从配置文件中加载初始化用户和角色数据
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "iteams.init-data")
public class InitialDataProperties {

    /**
     * 是否启用初始化数据
     */
    private boolean enabled = true;

    /**
     * 角色配置
     */
    private List<Role> roles = new ArrayList<>();

    /**
     * 超级管理员用户配置
     */
    private Admin adminUser = new Admin();

    /**
     * 角色配置类
     */
    @Data
    public static class Role {
        /**
         * 角色名称
         */
        private String name;

        /**
         * 角色编码
         */
        private String code;

        /**
         * 角色描述
         */
        private String description;
    }

    /**
     * 超级管理员用户配置类
     */
    @Data
    public static class Admin {
        /**
         * 用户名
         */
        private String username = "supadmin";

        /**
         * 密码（明文，会在使用时加密）或已加密的密码哈希
         */
        private String password = "Admin@123";

        /**
         * 密码是否已加密
         */
        private boolean passwordEncrypted = false;

        /**
         * 真实姓名
         */
        private String realName = "系统管理员";

        /**
         * 电子邮件
         */
        private String email = "admin@iteams.com";

        /**
         * 部门
         */
        private String department = "系统管理部";

        /**
         * 关联的角色编码
         */
        private String roleCode = "SUPER_ADMIN";
    }
} 