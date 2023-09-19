package com.qimu.qiapiinterface.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import icu.qimuu.qiapisdk.exception.BusinessException;
import icu.qimuu.qiapisdk.model.params.NameParams;
import icu.qimuu.qiapisdk.model.params.RandomWallpaperParams;
import icu.qimuu.qiapisdk.model.request.NameRequest;
import icu.qimuu.qiapisdk.model.response.NameResponse;
import icu.qimuu.qiapisdk.model.response.RandomWallpaperResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.qimu.qiapiinterface.utils.UrlUtils.buildUrl;


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
    public NameResponse getName(NameParams nameParams) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(nameParams), NameResponse.class);
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
}
