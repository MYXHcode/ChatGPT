package com.myxh.chatgpt.domain.validate;

import com.myxh.chatgpt.application.IWeiXinValidateService;
import com.myxh.chatgpt.infrastructure.util.sdk.SignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author MYXH
 * @date 2023/12/13
 * @description 微信公众号验签服务
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Service
public class WeiXinValidateServiceImpl implements IWeiXinValidateService
{
    @Value("${wx.config.token}")
    private String token;

    @Override
    public boolean checkSign(String signature, String timestamp, String nonce)
    {
        return SignatureUtil.check(token, signature, timestamp, nonce);
    }
}
