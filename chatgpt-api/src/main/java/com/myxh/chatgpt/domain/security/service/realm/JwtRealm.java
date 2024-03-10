package com.myxh.chatgpt.domain.security.service.realm;

import com.myxh.chatgpt.domain.security.model.vo.JwtToken;
import com.myxh.chatgpt.domain.security.service.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author MYXH
 * @date 2023/12/9
 * @description 自定义 Realm
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public class JwtRealm extends AuthorizingRealm
{
    private final Logger logger = LoggerFactory.getLogger(JwtRealm.class);

    private static final JwtUtil jwtUtil = new JwtUtil();

    @Override
    public boolean supports(AuthenticationToken token)
    {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    {
        // 暂时不需要实现
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException
    {
        String jwt = (String) token.getPrincipal();

        if (jwt == null)
        {
            throw new NullPointerException("jwtToken 不允许为空");
        }

        // 判断
        if (!jwtUtil.isVerify(jwt))
        {
            throw new UnknownAccountException();
        }

        // 可以获取 username 信息，并做一些处理
        String username = (String) jwtUtil.decode(jwt).get("username");
        logger.info("鉴权用户 username：{}", username);

        return new SimpleAuthenticationInfo(jwt, jwt, "JwtRealm");
    }
}
