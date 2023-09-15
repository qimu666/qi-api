package com.qimu.qiapibackend;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @Author: QiMu
 * @Date: 2023/09/15 09:33:19
 * @Version: 1.0
 * @Description: qi api后端应用程序
 */
@SpringBootApplication
@EnableScheduling
@EnableDubbo
@MapperScan("com.qimu.qiapibackend.mapper")
public class QiApiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiApiBackendApplication.class, args);
    }

}
