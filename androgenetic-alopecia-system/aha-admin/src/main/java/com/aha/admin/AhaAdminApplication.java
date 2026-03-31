package com.aha.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 雄脱健康管理系统管理端启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.aha")
@MapperScan("com.aha.dao.mapper")
public class AhaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AhaAdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("   雄脱健康管理系统启动成功！");
        System.out.println("========================================");
    }
}
