package com.blog.vo;

import lombok.Data;

/**
 * Meting 解析后的单曲信息（用于前台 HTML5 播放）
 */
@Data
public class MetingSongVO {

    private String name;
    private String artist;
    /** 可供 audio 标签播放的地址（直链或 Meting 代理 URL） */
    private String url;
    private String pic;
}
