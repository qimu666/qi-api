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
     * 援引
     * 接口调用
     *
     * @param interfaceInfoId 接口信息id
     * @param userId          用户id
     * @param reduceScore     降低分数
     * @return boolean
     */
    boolean invoke(Long interfaceInfoId, Long userId, Integer reduceScore);
}
