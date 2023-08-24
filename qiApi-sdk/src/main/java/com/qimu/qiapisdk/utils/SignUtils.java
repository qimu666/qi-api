package com.qimu.qiapisdk.utils;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;

/**
 * @Author: QiMu
 * @Date: 2023年08月16日 12:22
 * @Version: 1.0
 * @Description:
 */
public class SignUtils {
    public static String getSign(String body, String secretKey) {
        String s = MD5.create().digestHex(JSONUtil.toJsonStr(body) + '.' + secretKey);
        System.err.println(s);
        return s;
    }
}
