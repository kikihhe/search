package com.xiaohe.search2.domain;


import lombok.Data;

@Data
public class SearchResult {
    private String title;

    private String url;

    // 描述，从正文中提取
    private String description;




    public SearchResult() {

    }

    public SearchResult(String title, String url, String description) {
        this.title = title;
        this.url = url;
        this.description = description;
    }


}
