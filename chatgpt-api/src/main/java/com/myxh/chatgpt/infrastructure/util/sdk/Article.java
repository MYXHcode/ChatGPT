package com.myxh.chatgpt.infrastructure.util.sdk;

import lombok.Getter;

@Getter
public class Article
{
    private String title;
    private String description;
    private String picUrl;
    private String url;

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
