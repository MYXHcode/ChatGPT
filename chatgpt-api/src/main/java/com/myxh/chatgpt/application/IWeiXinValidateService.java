package com.myxh.chatgpt.application;

/**
 * @author MYXH
 * @date 2023/12/13
 * @description 微信公众号验签服务
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public interface IWeiXinValidateService
{
    boolean checkSign(String signature, String timestamp, String nonce);
}
