package com.qimu.qiapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qimu.qiapibackend.model.entity.InterfaceInfo;
import com.qimu.qiapibackend.model.entity.UserInterfaceInvoke;
import com.qimu.qiapibackend.model.vo.UserVO;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:29:54
 * @Version: 1.0
 * @Description: 用户界面调用服务
 */
public interface UserInterfaceInvokeService extends IService<UserInterfaceInvoke> {

    /**
     * 接口调用
     *
     * @param interfaceInfo 接口信息
     * @param userVO        用户vo
     * @return boolean
     */
    boolean invoke(InterfaceInfo interfaceInfo, UserVO userVO);
}
