package com.qimu.qiapisdk;

import com.qimu.qiapisdk.client.QiApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: QiMu
 * @Date: 2023年08月17日 21:09
 * @Version: 1.0
 * @Description:
 */
@Data
@Configuration
@ConfigurationProperties("qi.api.client")
@ComponentScan
public class QiApiClientConfig {
    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 秘密密钥
     */
    private String secretKey;

    @Bean
    public QiApiClient qiApiClient() {
        return new QiApiClient(accessKey, secretKey);
    }
}
