package com.qimu.qiapiinterface;


import com.qimu.qiapisdk.client.QiApiClient;
import com.qimu.qiapisdk.common.BaseResponse;
import com.qimu.qiapisdk.model.QiApiRequest;
import com.qimu.qiapisdk.model.User;
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

    @Test
    void contextLoads() {
        // QiApiClient qiApiClient = new QiApiClient("7052a8594339a519e0ba5eb04a267a60", "d8d6df60ab209385a09ac796f1dfe3e1");
        QiApiRequest qiApiRequest = new QiApiRequest();
        qiApiRequest.setName("qimu");
        BaseResponse<User> nameByJsonPost = qiApiClient.getNameByJsonPost(qiApiRequest);
        System.out.println("nameByJsonPost = " + nameByJsonPost);
    }

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
        String line = "";
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
