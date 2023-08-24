package com.qimu.qiapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.mapper.InterfaceInfoMapper;
import com.qimu.qiapibackend.model.entity.InterfaceInfo;
import com.qimu.qiapibackend.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author qimu
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-08-11 13:14:10
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        Long userId = interfaceInfo.getUserId();
        String method = interfaceInfo.getMethod();
        String requestParams = interfaceInfo.getRequestParams();
        String description = interfaceInfo.getDescription();
        String requestExample = interfaceInfo.getRequestExample();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        Integer status = interfaceInfo.getStatus();

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, url, method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }
}




