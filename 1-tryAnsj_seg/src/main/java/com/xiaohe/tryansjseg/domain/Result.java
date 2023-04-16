package com.xiaohe.tryansjseg.domain;



public class Result {
    private String title;

    private String url;

    // 描述，从正文中提取
    private String description;

    public Result() {

    }

    public Result(String title, String url, String description) {
        this.title = title;
        this.url = url;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Result{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
