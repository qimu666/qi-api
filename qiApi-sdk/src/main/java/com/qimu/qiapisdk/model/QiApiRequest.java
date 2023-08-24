package com.qimu.qiapisdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023/08/17 09:18:33
 * @Version: 1.0
 * @Description: api请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QiApiRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;

}