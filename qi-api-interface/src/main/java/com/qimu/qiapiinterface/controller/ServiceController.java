package com.qimu.qiapiinterface.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import icu.qimuu.qiapisdk.exception.BusinessException;
import icu.qimuu.qiapisdk.exception.ErrorCode;
import icu.qimuu.qiapisdk.model.params.RandomWallpaperParams;
import icu.qimuu.qiapisdk.model.response.RandomWallpaperResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;


/**
 * @Author: QiMu
 * @Date: 2023年08月16日 11:29
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/")
public class ServiceController {

    @GetMapping("name")
    public String getName(String name) {
        return name;
    }

    @GetMapping("/poisonousChickenSoup")
    public String getPoisonousChickenSoup() {
        HttpResponse execute = HttpRequest.get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json").execute();
        return execute.body();
    }

    @GetMapping("/randomWallpaper")
    public RandomWallpaperResponse randomWallpaper(RandomWallpaperParams randomWallpaperParams) throws BusinessException {
        String baseUrl = "https://api.btstu.cn/sjbz/api.php";
        String url = buildUrl(baseUrl, randomWallpaperParams);
        if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
            url = url + "?format=json";
        } else {
            url = url + "&format=json";
        }
        HttpResponse execute = HttpRequest.get(url).execute();
        String body = execute.body();
        return JSONUtil.toBean(body, RandomWallpaperResponse.class);
    }

    public <T> String buildUrl(String baseUrl, T params) throws BusinessException {
        StringBuilder url = new StringBuilder(baseUrl);
        Field[] fields = params.getClass().getDeclaredFields();
        boolean isFirstParam = true;
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            // 跳过serialVersionUID属性
            if ("serialVersionUID".equals(name)) {
                continue;
            }
            try {
                Object value = field.get(params);
                if (value != null) {
                    if (isFirstParam) {
                        url.append("?").append(name).append("=").append(value);
                        isFirstParam = false;
                    } else {
                        url.append("&").append(name).append("=").append(value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "构建url异常");
            }
        }
        return url.toString();
    }
}
