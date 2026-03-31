package com.hairloss.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 雄激素性脱发患者管理与分析系统启动类
 */
@SpringBootApplication
public class HairLossSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(HairLossSystemApplication.class, args);
        System.out.println("========================================");
        System.out.println("雄激素性脱发患者管理与分析系统启动成功！");
        System.out.println("接口文档地址：http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
