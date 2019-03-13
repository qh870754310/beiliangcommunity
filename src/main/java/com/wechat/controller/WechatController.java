package com.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.wechat.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 微信控制器
 * Created by zhengda.li on 2017/6/21 15:33.
 */
@Controller
public class WechatController {
    @Value("${wechat.app-id}")
    private String appId;
    @Value("${wechat.app-secret}")
    private String appSecret;
    @Value("${wechat.token}")
    private String token;
    @Value("${wechat.api-domain}")
    private String apiDomain;


    @Resource
    private RedisTokenHelper redisTokenHelper;

    /**
     * 微信公众号验证对接
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     */
    @GetMapping("/wechat")
    public void verify(HttpServletRequest request, HttpServletResponse response) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        String[] ArrTmp = {token, timestamp, nonce};
        Arrays.sort(ArrTmp);
        StringBuilder sb = new StringBuilder();
        for (String aArrTmp : ArrTmp) {
            sb.append(aArrTmp);
        }
        String pwd = Encrypt(sb.toString());
        assert pwd != null;
        if (pwd.trim().equals(signature.trim())) {
            ResponseHelper.write(response, echostr);
        }
    }

    /**
     * 消息回复
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     */
    @PostMapping("/wechat")
    public void resMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuffer msg = new StringBuffer();
        Map<String, String> map = MessageHelper.parseXml(request);
        // 发送方帐号（一个OpenID）
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        // 消息类型
        String msgType = map.get("MsgType");
        if (msgType.equals("event")) {//关注/取消事件
            String event = map.get("Event");
            if (event.equals("subscribe")) {//订阅事件
                msg.append("<xml>");
                msg.append("<ToUserName><![CDATA[").append(fromUserName).append("]]></ToUserName>");
                msg.append("<FromUserName><![CDATA[").append(toUserName).append("]]></FromUserName>");
                msg.append("<CreateTime>12345678</CreateTime>");
                msg.append("<MsgType><![CDATA[text]]></MsgType>");
                msg.append("<Content><![CDATA[欢迎关注！]]></Content>");
                msg.append("</xml>");
                ResponseHelper.write(response, msg);
            }
        }
        if (msgType.equals("text")) {
            String responseMessage = "我听不懂你在说什么!";//默认回复
            msg.append("<xml>");
            msg.append("<ToUserName><![CDATA[").append(fromUserName).append("]]></ToUserName>");
            msg.append("<FromUserName><![CDATA[").append(toUserName).append("]]></FromUserName>");
            msg.append("<CreateTime>12345678</CreateTime>");
            msg.append("<MsgType><![CDATA[text]]></MsgType>");
            msg.append("<Content><![CDATA[").append(responseMessage).append("]]></Content>");
            msg.append("</xml>");
            ResponseHelper.write(response, msg);
        }
    }

    /**
     * 获取AccessToken
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     */
    @RequestMapping("/getAccessToken")
    public void getAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*String appID = request.getParameter("appID");
        String appsecret = request.getParameter("appsecret");*/
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        URL reqURL = new URL(requestUrl);
        HttpsURLConnection http = (HttpsURLConnection) reqURL.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setDoOutput(true);
        http.setDoInput(true);
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
        System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒
        http.connect();
        InputStream is = http.getInputStream();
        int size = is.available();
        byte[] jsonBytes = new byte[size];
        is.read(jsonBytes);
        String message = new String(jsonBytes, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(message);
        ResponseHelper.write(response, jsonObject);
    }

    /**
     * 创建菜单
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     */
    @RequestMapping("/createMenu")
    public void createMenu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = request.getParameter("accessToken");
        String menuData = request.getParameter("menuData");
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
        URL url = new URL(requestUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setDoOutput(true);
        http.setDoInput(true);
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
        System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒

        http.connect();
        OutputStream os = http.getOutputStream();
        os.write(menuData.getBytes("UTF-8"));//传入参数
        os.flush();
        os.close();
        InputStream is = http.getInputStream();
        int size = is.available();
        byte[] jsonBytes = new byte[size];
        is.read(jsonBytes);
        String message = new String(jsonBytes, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(message);
        ResponseHelper.write(response, jsonObject);
    }

    /**
     * 删除菜单
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     */
    @RequestMapping("/deleteMenu")
    public void deleteMenu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = request.getParameter("accessToken");
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + accessToken;
        URL url = new URL(requestUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setDoOutput(true);
        http.setDoInput(true);
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
        System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒

        http.connect();
        InputStream is = http.getInputStream();
        int size = is.available();
        byte[] jsonBytes = new byte[size];
        is.read(jsonBytes);
        String message = new String(jsonBytes, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(message);
        ResponseHelper.write(response, jsonObject);
    }


    /**
     * 跳转页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * 获取用户的openid
     * 获取code后，请求以下链接获取access_token：https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     * 网页授权流程分为四步：
     * 1、引导用户进入授权页面同意授权，获取code
     * 2、通过code换取网页授权access_token（与基础支持中的access_token不同）
     * 3、如果需要，开发者可以刷新网页授权access_token，避免过期
     * 4、通过网页授权access_token和openid获取用户基本信息（支持UnionID机制）
     * 用户访问第三方页面时，先去请求一个api，获取code和state
     * code说明 ： code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    @RequestMapping("/toPage")
    public ModelAndView toPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelAndView mav = new ModelAndView();
        String code = request.getParameter("code");//获取微信服务器授权返回的code值
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
        URL url = new URL(requestUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setDoOutput(true);
        http.setDoInput(true);
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
        System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒

        http.connect();
        InputStream is = http.getInputStream();
        int size = is.available();
        byte[] jsonBytes = new byte[size];
        is.read(jsonBytes);
        String message = new String(jsonBytes, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(message);

        /*String openId = jsonObject.get("openid").toString();
        requestUrl = apiDomain + "/api/wechat/queryBind?openId=" + openId + "&category=1";
        url = new URL(requestUrl);
        http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setDoOutput(true);
        http.setDoInput(true);
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
        System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒
        http.connect();
        is = http.getInputStream();
        size = is.available();
        jsonBytes = new byte[size];
        is.read(jsonBytes);
        message = new String(jsonBytes, "UTF-8");
        jsonObject = JSONObject.parseObject(message);
        if (jsonObject.getBooleanValue("state")) {//已绑定
            mav.addObject("bindState", true);
            JSONObject jsonObject1 = jsonObject.getJSONObject("bindInfo");
        } else {//未绑定
            mav.addObject("bindState", false);
        }*/
        mav.addObject("apiDomain", apiDomain);
       // mav.addObject("openId", openId);
        mav.setViewName("wechat/default");
        return mav;
    }

    @RequestMapping(value = "/config")
    public void _getAccessToken(String url, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取jsapi_ticket
        String jsapi_ticket = RedisUtils.get("global_jsapi_ticket");
        if (StringUtils.isEmpty(jsapi_ticket)) {
            WeChatApi weChatApi = new WeChatApi();
            //获取access_token
            String global_access_token = RedisUtils.get("global_access_token");
            if (StringUtils.isEmpty(global_access_token)) {
                global_access_token = weChatApi.getAccessToken(appId, appSecret);
            }
            jsapi_ticket = weChatApi.getJsApiTicket(global_access_token);
        }
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString();
        // 签名
        String sign = signJsSDK(jsapi_ticket, url, timestamp, nonceStr);
        Map resMap = new HashMap();
        resMap.put("appId", appId);
        resMap.put("ticket", jsapi_ticket);
        resMap.put("signature", sign);
        resMap.put("nonceStr", nonceStr);
        resMap.put("timestamp", timestamp);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resMap", resMap);
        ResponseHelper.write(response, jsonObject);
    }


    /**
     * 签名方法
     *
     * @param ticket
     * @param url
     * @param timestamp
     * @param nonceStr
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String signJsSDK(String ticket, String url, String timestamp, String nonceStr) throws Exception {
        Map map = new HashMap();
        String signStr;
        String signature = "";
        /*map.put("noncestr", nonceStr);
        map.put("jsapi_ticket", ticket);
        map.put("timestamp", timestamp);
        map.put("url", url);
        String[] ss = {"noncestr", "jsapi_ticket", "timestamp", "url"};
        Arrays.sort(ss);
        String signStr = "";
        for (String s : ss) {
            signStr += s + "=" + map.get(s) + "&";
        }*/
        // 注意这里参数名必须全部小写，且必须有序
        signStr = "jsapi_ticket=" + ticket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(signStr.getBytes("UTF-8"));
        signature = byteToHex(crypt.digest());
        return signature;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private String Encrypt(String strSrc) {
        MessageDigest md;
        String strDes;

        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Invalid algorithm.");
            return null;
        }
        return strDes;
    }

    private String bytes2Hex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp;
        for (byte bt : bts) {
            tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }
}
