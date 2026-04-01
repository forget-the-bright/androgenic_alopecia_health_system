package com.hairloss.system;

import com.hairloss.system.utils.ColorTextPlus;
import org.hao.core.print.ColorText;
import org.hao.core.print.PrintUtil;
import org.hao.spring.SpringRunUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 雄激素性脱发患者管理与分析系统启动类
 */
@SpringBootApplication
public class HairLossSystemApplication {

    public static void main(String[] args) {
        SpringRunUtil.runAfter(HairLossSystemApplication.class, args,context->{
            ColorTextPlus.printRainbow(" 雄 激 素 性 脱 发 患 者 管 理 与 分 析 系 统 启 动 成 功 ！",true);
        });
    }
}
