package com.qimu.qiapibackend.model.dto.interfaceinfo;

import lombok.Data;

/**
 * @Author: QiMu
 * @Date: 2023年09月22日 17:40
 * @Version: 1.0
 * @Description:
 */
@Data
public class InterfaceInfoUpdateAvatarRequest {
    private static final long serialVersionUID = 1L;
    private long id;
    /**
     * 接口头像
     */
    private String avatarUrl;
}
