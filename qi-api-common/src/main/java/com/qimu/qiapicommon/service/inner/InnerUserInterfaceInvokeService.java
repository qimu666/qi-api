package com.qimu.qiapicommon.service.inner;

import com.qimu.qiapicommon.model.entity.InterfaceInfo;
import com.qimu.qiapicommon.model.vo.UserVO;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:29:54
 * @Version: 1.0
 * @Description: 用户界面调用服务
 */
public interface InnerUserInterfaceInvokeService {

    /**
     * 接口调用
     *
     * @param interfaceInfo 接口信息
     * @param userVO        用户vo
     * @return boolean
     */
    boolean invoke(InterfaceInfo interfaceInfo, UserVO userVO);
}
