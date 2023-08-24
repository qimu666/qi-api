package com.qimu.qiapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qimu.qiapibackend.model.entity.InterfaceInfo;

/**
 * @author qimu
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-08-11 13:14:10
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param add           是否为创建校验
     * @param interfaceInfo 接口信息
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
