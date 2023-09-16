package com.qimu.qiapiinterface.controller;

import com.qimuu.easyweb.common.BaseResponse;
import com.qimuu.easyweb.common.ResultUtils;
import icu.qimuu.qiapisdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: QiMu
 * @Date: 2023年08月16日 11:29
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping
    public BaseResponse<String> getNameByGet(String name) {
        return ResultUtils.success(name);
    }

    @PostMapping
    public BaseResponse<String> getNameByPost(String name) {
        return ResultUtils.success(name);
    }

    @PostMapping(value = "/json")
    public BaseResponse<User> getNameByJsonPost(@RequestBody User user, HttpServletRequest request) {
        return ResultUtils.success(user);
    }
}
