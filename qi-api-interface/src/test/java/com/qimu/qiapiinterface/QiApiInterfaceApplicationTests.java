package com.qimu.qiapiinterface;


import icu.qimuu.qiapisdk.client.QiApiClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

@SpringBootTest
@Slf4j
class QiApiInterfaceApplicationTests {
    @Resource
    private QiApiClient qiApiClient;


    @Resource
    private JavaMailSender mailSender;

    @Test
    void testEmail() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // 获取验证码 存入redis
            int captcha = (int) ((Math.random() * 9 + 1) * 100000);

            // 邮箱发送内容组成
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("验证码邮件");
            helper.setText(buildContent(String.valueOf(captcha)), true);
            helper.setTo("1924972446@qq.com");
            helper.setFrom("Qi-API 接口开放平台" + '<' + "2483482026@qq.com" + '>');
            mailSender.send(message);
        } catch (Exception e) {
            log.error("【发送失败】" + e.getMessage());
        }
    }

    public String buildContent(String captcha) {
        // 加载邮件html模板
        ClassPathResource resource = new ClassPathResource("email.html");
        InputStream inputStream = null;
        BufferedReader fileReader = null;
        StringBuilder buffer = new StringBuilder();
        String line;
        try {
            inputStream = resource.getInputStream();
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = fileReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            log.info("发送邮件读取模板失败{}", e.getMessage());
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 替换html模板中的参数
        return MessageFormat.format(buffer.toString(), captcha);
    }
}
