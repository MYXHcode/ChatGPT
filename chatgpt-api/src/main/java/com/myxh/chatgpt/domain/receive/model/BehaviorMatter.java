package com.myxh.chatgpt.domain.receive.model;

import lombok.Getter;

import java.util.Date;

/**
 * @author MYXH
 * @date 2023/12/13
 * @description 微信公众号验签服务
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Getter
public class BehaviorMatter
{
    private String openId;
    private String fromUserName;
    private String msgType;
    private String content;
    private String event;
    private Date createTime;

    public void setOpenId(String openId)
    {
        this.openId = openId;
    }

    public void setFromUserName(String fromUserName)
    {
        this.fromUserName = fromUserName;
    }

    public void setMsgType(String msgType)
    {
        this.msgType = msgType;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
