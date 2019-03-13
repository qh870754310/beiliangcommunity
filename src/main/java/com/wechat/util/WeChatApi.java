package com.wechat.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by Administrator on 2018/3/16.
 */
@Component
public class WeChatApi {

    private final Logger logger = LoggerFactory.getLogger(WeChatApi.class);

    @Resource
    private RedisTokenHelper redisTokenHelper;

    /**
     * 获取access_token
     *
     * @return access_token
     * @param appId
     * @param appSecret
     */
    public String getAccessToken(String appId, String appSecret) throws SQLException {
        logger.info("==============开始获取access_token===============");
        String access_token = null;
        String grant_type = "client_credential";
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type="+grant_type+"&appid="+ appId +"&secret="+ appSecret;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            access_token = demoJson.getString("access_token");
            is.close();
            logger.info("==============结束获取access_token===============");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("==============开始写入access_token===============");
        RedisUtils.set("global_access_token", access_token, 7200);
        logger.info("==============写入access_token成功===============");
        return access_token;
    }

    /**
     * 获取jsapi_ticket
     *
     * @param accessToken
     * @return  jsapi_ticket
     */
    public String getJsApiTicket(String accessToken) throws SQLException {
        logger.info("==============开始获取jsapi_ticket===============");
        String jsapi_ticket = null;
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            jsapi_ticket = demoJson.getString("ticket");
            is.close();
            logger.info("==============结束获取jsapi_ticket===============");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("==============开始写入jsapi_ticket===============");
        RedisUtils.set("global_jsapi_ticket", jsapi_ticket, 7200);
        logger.info("==============写入jsapi_ticket成功===============");
        return jsapi_ticket;
    }

}
