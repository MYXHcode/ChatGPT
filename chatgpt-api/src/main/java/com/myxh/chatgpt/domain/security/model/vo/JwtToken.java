package com.myxh.chatgpt.domain.security.model.vo;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author MYXH
 * @date 2023/12/9
 * @description Token 信息
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public class JwtToken implements AuthenticationToken
{
    private final String jwt;

    public JwtToken(String jwt)
    {
        this.jwt = jwt;
    }

    /**
     * 等同于账户
     */
    @Override
    public Object getPrincipal()
    {
        return jwt;
    }

    /**
     * 等同于密码
     */
    @Override
    public Object getCredentials()
    {
        return jwt;
    }
}
