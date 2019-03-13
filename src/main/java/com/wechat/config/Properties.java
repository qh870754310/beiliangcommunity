package com.wechat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 扩展属性
 * Created by zhengda.li on 2017/6/21 15:51.
 */
@Component
@ConfigurationProperties(prefix = "wechat")
public class Properties {
    /**
     * appID
     */
    private String appId;

    /**
     * appsecret
     */
    private String appSecret;
    /**
     * 微信接入Token
     */
    private String token;

    /**
     * 本地域名
     */
    private String localDomain;

    /**
     * api接口域名
     */
    private String apiDomain;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getLocalDomain() {
        return localDomain;
    }

    public void setLocalDomain(String localDomain) {
        this.localDomain = localDomain;
    }

    public String getApiDomain() {
        return apiDomain;
    }

    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
    }
}
