package com.qimu.qiapibackend.service.inner.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qimu.qiapibackend.service.InterfaceInfoService;
import com.qimu.qiapicommon.model.entity.InterfaceInfo;
import com.qimu.qiapicommon.service.inner.InnerInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author: QiMu
 * @Date: 2023年09月15日 22:53
 * @Version: 1.0
 * @Description:
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        // 如果带参数，去除第一个？和之后后的参数
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceInfo::getMethod, method);
        lambdaQueryWrapper.eq(InterfaceInfo::getUrl, path);
        return interfaceInfoService.getOne(lambdaQueryWrapper);
    }
}
