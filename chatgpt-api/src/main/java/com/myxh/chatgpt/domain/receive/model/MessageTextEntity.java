package com.myxh.chatgpt.domain.receive.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;

/**
 * @author MYXH
 * @date 2023/12/13
 * @description 消息文本实体
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Getter
public class MessageTextEntity
{
    @XStreamAlias("MsgId")
    private String msgId;

    @XStreamAlias("ToUserName")
    private String toUserName;

    @XStreamAlias("FromUserName")
    private String fromUserName;

    @XStreamAlias("CreateTime")
    private String createTime;

    @XStreamAlias("MsgType")
    private String msgType;

    @XStreamAlias("Content")
    private String content;

    @XStreamAlias("Event")
    private String event;

    @XStreamAlias("EventKey")
    private String eventKey;

    public MessageTextEntity()
    {

    }

    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }

    public void setToUserName(String toUserName)
    {
        this.toUserName = toUserName;
    }

    public void setFromUserName(String fromUserName)
    {
        this.fromUserName = fromUserName;
    }

    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
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

    public void setEventKey(String eventKey)
    {
        this.eventKey = eventKey;
    }
}
