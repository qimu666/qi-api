package com.qimu.qiapigateway;

import com.qimu.qiapibackend.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @Author: QiMu
 * @Date: 2023/09/15 08:01:39
 * @Version: 1.0
 * @Description: qi api网关应用程序
 */
@SpringBootApplication
@EnableDubbo
@Service
public class QiApiGatewayApplication {

    @DubboReference
    private DemoService demoService;

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(QiApiGatewayApplication.class, args);
        QiApiGatewayApplication bean = run.getBean(QiApiGatewayApplication.class);
        String doSayHello = bean.doSayHello("world");
        System.err.println(doSayHello);
        String sayHello2 = bean.sayHello2("你好");
        System.err.println(sayHello2);

    }

    private String doSayHello(String world) {
        return demoService.sayHello(world);
    }


    private String sayHello2(String world) {
        return demoService.sayHello2(world);
    }
}
