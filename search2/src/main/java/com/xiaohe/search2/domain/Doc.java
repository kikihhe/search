package com.xiaohe.search2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-04-09 20:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Doc {
    /**
     * 唯一ID，主键
     */
    private int docId;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文章在线链接
     */
    private String url;

    /**
     * 文章内容
     */
    private String content;

    public Doc(String title, String url, String content) {
        this.title = title;
        this.url = url;
        this.content = content;
    }
}
