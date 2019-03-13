package com.wechat.message.req;

/**
 * 请求消息之图片消息
 *
 * Created by Administrator on 2017/11/13.
 */
public class ImageMessage extends BaseMessage {
    //图片链接
    private String PicUrl;

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }
}
