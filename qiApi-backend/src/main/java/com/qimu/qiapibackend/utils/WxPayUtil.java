package com.qimu.qiapibackend.utils;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: QiMu
 * @Date: 2023/09/03 08:51:32
 * @Version: 1.0
 * @Description: wx 签名工具类
 */
public class WxPayUtil {
    /**
     * 获取回调请求头：签名相关
     *
     * @param request HttpServletRequest
     * @return SignatureHeader
     */
    public static SignatureHeader getRequestHeader(HttpServletRequest request) {
        // 获取通知签名
        String signature = request.getHeader("Wechatpay-Signature");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String serial = request.getHeader("Wechatpay-Serial");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(signature);
        signatureHeader.setNonce(nonce);
        signatureHeader.setSerial(serial);
        signatureHeader.setTimeStamp(timestamp);
        return signatureHeader;
    }
}
