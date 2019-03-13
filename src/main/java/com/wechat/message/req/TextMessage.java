package com.wechat.message.req;

/**
 * 请求消息之文本消息
 *
 * Created by Administrator on 2017/11/13.
 */
public class TextMessage extends BaseMessage {
    //消息内容
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
