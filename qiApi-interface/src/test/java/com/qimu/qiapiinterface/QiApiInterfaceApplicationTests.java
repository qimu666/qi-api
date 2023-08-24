package com.qimu.qiapiinterface;


import com.qimu.qiapisdk.client.QiApiClient;
import com.qimu.qiapisdk.common.BaseResponse;
import com.qimu.qiapisdk.model.QiApiRequest;
import com.qimu.qiapisdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
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

    @Test
    void test() {
    }
}
