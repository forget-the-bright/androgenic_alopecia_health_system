package com.androgenic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 雄脱健康管理系统启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.androgenic")
@MapperScan("com.androgenic.**.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("========================================");
        System.out.println("   雄脱健康管理系统启动成功！");
        System.out.println("========================================");
    }
}
