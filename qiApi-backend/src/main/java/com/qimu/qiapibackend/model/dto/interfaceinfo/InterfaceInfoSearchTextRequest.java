package com.qimu.qiapibackend.model.dto.interfaceinfo;

import com.qimu.qiapibackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023年08月19日 10:10
 * @Version: 1.0
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoSearchTextRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = -6337349622479990038L;

    private String searchText;
}
