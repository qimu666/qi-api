package com.qimu.qiapisdk.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023年08月17日 21:22
 * @Version: 1.0
 * @Description:
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -6719271155533429560L;
    private String name;
}
