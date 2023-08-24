package com.qimu.qiapisdk.client;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.qimu.qiapisdk.common.BaseResponse;
import com.qimu.qiapisdk.model.QiApiRequest;
import com.qimu.qiapisdk.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static com.qimu.qiapisdk.utils.SignUtils.getSign;

/**
 * @Author: QiMu
 * @Date: 2023年08月16日 11:33
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QiApiClient {
    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 秘密密钥
     */
    private String secretKey;

    /**
     * 被json职位名称
     *
     * @param qiApiRequest api请求
     * @return {@link BaseResponse}<{@link String}>
     */
    public BaseResponse<User> getNameByJsonPost(QiApiRequest qiApiRequest) {
        HttpResponse httpResponse = HttpRequest.post("http://localhost:8081/name/json")
                .addHeaders(getHeaders(JSONUtil.toJsonStr(qiApiRequest)))
                .body(JSONUtil.toJsonStr(qiApiRequest))
                .execute();

        int status = httpResponse.getStatus();
        System.out.println(status);
        System.out.println(httpResponse.body());
        TypeReference<BaseResponse<User>> typeRef = new TypeReference<BaseResponse<User>>() {
        };

        System.out.println("typeRef = " + typeRef);
        return JSONUtil.toBean(httpResponse.body(), typeRef, false);
    }

    private Map<String, String> getHeaders(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        String encodedBody = SecureUtil.md5(body);
        hashMap.put("body", encodedBody);
        hashMap.put("timestamp", String.valueOf((System.currentTimeMillis() + 5 * 60 * 1000) / 1000));
        hashMap.put("sign", getSign(encodedBody, secretKey));
        return hashMap;
    }
}
