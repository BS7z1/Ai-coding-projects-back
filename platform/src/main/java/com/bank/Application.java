package com.bank;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 业务管理系统启动类
 */
@SpringBootApplication(scanBasePackages = {"com.bank"})
@MapperScan(basePackages = "com.bank", markerInterface = BaseMapper.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("========================================");
        System.out.println("业务管理系统启动成功！");
        System.out.println("========================================");
    }
}
