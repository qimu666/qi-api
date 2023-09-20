package com.qimu.qiapibackend.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request.Amount;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ijpay.alipay.AliPayApi;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapicommon.model.entity.InterfaceInfo;
import com.qimu.qiapicommon.model.vo.UserVO;
import com.qimu.qiapicommon.service.inner.InnerUserInterfaceInvokeService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;


/**
 * 用户服务测试
 *
 * @author qimu
 */
@SpringBootTest
class UserServiceTest {

    /**
     * 商户API私钥路径
     */

    private static final String Secret = "b81f683a36eccba0f8656dc402281d12";
    /**
     * 商户号
     */
    public static String merchantId = "1609724068";
    public static String privateKeyPath = "D:\\Program Files\\WXCertUtil\\cert\\1609724068_20230822_cert\\apiclient_key.pem";
    /**
     * 商户证书序列号
     */
    public static String merchantSerialNumber = "763256B437DFF0C8BEBD27E012BFFD134FDBD9A6";
    /**
     * 商户APIV3密钥
     */
    public static String apiV3Key = "bf389934bf389934bf389934bf389934";

    @Resource
    private WxPayService wxPayService;
    @Resource
    private UserService userService;
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private InnerUserInterfaceInvokeService innerUserInterfaceInvokeService;

    @Test
    void getCaptcha() {
        String captcha = RandomUtil.randomNumbers(6);
        System.err.println(captcha);
    }

    @Test
    void invoke() {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(1697274658264342530L);

        User user = userService.getById(1691069533871013889L);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // userVO.setBalance(0);
        // userVO.setStatus(UserAccountStatusEnum.BAN.getValue());
        com.qimu.qiapicommon.model.entity.InterfaceInfo newInterfaceInfo = new com.qimu.qiapicommon.model.entity.InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfo, newInterfaceInfo);
        boolean invoke = innerUserInterfaceInvokeService.invoke(newInterfaceInfo.getId(), user.getId(), interfaceInfo.getReduceScore());
        Assertions.assertTrue(invoke);
    }

    @Test
    void date() {

        Date date = DateUtil.date(System.currentTimeMillis());
        System.err.println(date + "=data");
        DateTime offset = DateUtil.offset(date, DateField.MINUTE, 5);
        System.err.println(offset + "=offset+5");

        String format = DateUtil.format(offset, "yyyy-MM-dd'T'HH:mm:ssXXX");
        System.err.println(format);

        // System.err.println(RandomUtil.randomNumbers(20));
    }

    @Test
    void header() {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(1697274658264342530L);
        interfaceInfo.setRequestHeader(null);
        String requestHeader = interfaceInfo.getRequestHeader();
        Map<String, String> headerMap = new Gson().fromJson(requestHeader, new TypeToken<Map<String, String>>() {
        }.getType());
        if ((ObjectUtils.anyNull(headerMap, headerMap.get("Content-Type"))) || !headerMap.get("Content-Type").equals("application/json")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求头/响应头必须是JSON格式");
        }
    }

    @SneakyThrows
    @Test
    void pay() {
        // WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
        // Amount amount = new Amount();
        // amount.setTotal(10);
        // request.setAmount(amount);
        // request.setMchid("1609724068");
        // request.setDescription("测试商品标题");
        // request.setNotifyUrl("https://qimuu.icu/");
        // request.setOutTradeNo("order_162226155111116789");

        // String v3 = wxPayService.createOrderV3(TradeTypeEnum.NATIVE, request);
        // System.err.println(v3);

        WxPayRefundV3Request wxPayRefundV3Request = new WxPayRefundV3Request();
        // wxPayRefundV3Request.setTransactionId("4200001939202308225870750928");
        wxPayRefundV3Request.setOutTradeNo("order_06318873058334672021");
        wxPayRefundV3Request.setOutRefundNo("order_06318873058334672021");
        wxPayRefundV3Request.setReason("重复购买");
        WxPayRefundV3Request.Amount amount1 = new WxPayRefundV3Request.Amount();
        amount1.setRefund(1);
        amount1.setTotal(1);
        amount1.setCurrency("CNY");
        wxPayRefundV3Request.setAmount(amount1);
        WxPayRefundV3Result wxPayRefundV3Result = wxPayService.refundV3(wxPayRefundV3Request);
        System.err.println(wxPayRefundV3Result);


    }

    @Test
    void refund() throws AlipayApiException {
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo("order_44952882910123906867");
        model.setRefundAmount("0.01");
        model.setRefundReason("重复购买");
        AlipayTradeRefundResponse alipayTradeRefundResponse = AliPayApi.tradeRefundToResponse(model);
        System.err.println(alipayTradeRefundResponse);
    }

    @Test
    void testAddUser() {
        User user = new User();
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    @Test
    void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

}