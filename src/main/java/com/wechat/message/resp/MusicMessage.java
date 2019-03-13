package com.wechat.message.resp;

/**
 * 响应消息之音乐消息
 *
 * Created by Administrator on 2017/11/13.
 */
public class MusicMessage extends BaseMessage {

    // 音乐
    private Music Music;

    public Music getMusic() {
        return Music;
    }

    public void setMusic(Music music) {
        Music = music;
    }
}
