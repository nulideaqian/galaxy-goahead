package com.galaxy.goahead.oauth2demo.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Galaxy
 * @since 2022/5/5 1:23
 */
@Controller
public class LoginController {

  private final String githubClientId = "f7b554237b0b38e36f41";

  private final String githubClientSecret = "a18040208eb5053c861a61555ec2e84f343fd0d7";

  @GetMapping("/oauth/redirect")
  private String getAccessToken(@RequestParam("code") String code) {
    // 基于授权码 请求令牌
    String urlTemplate = "https://github.com/login/oauth/access_token?client_id={}&client_secret={}&code={}";
    String url = StrUtil.format(urlTemplate, githubClientId, githubClientSecret, code);
    String body = HttpUtil.createPost(url)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .timeout(20000)
        .execute().body();
    Map<String, String> bodyMap = HttpUtil.decodeParamMap(body, StandardCharsets.UTF_8);

    // 请求用户信息
    String userInfoStr = HttpUtil.createGet("https://api.github.com/user")
        .auth("bearer " + bodyMap.get("access_token"))
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .execute().body();
    JSONObject bodyJson = JSONObject.parseObject(userInfoStr, JSONObject.class);
    return "redirect:/welcome.html?name=" + bodyJson.getString("name");
  }

}
