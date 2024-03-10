package com.myxh.chatgpt.domain.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author MYXH
 * @date 2023/12/9
 * @description 获取 JwtToken，获取 JwtToken 中封装的信息，判断 JwtToken 是否存在
 * 1. encode()，参数是=签发人，存在时间，一些其他的信息。返回值是 JwtToken 对应的字符串
 * 2. decode()，参数是=JwtToken=。返回值是荷载部分的键值对
 * 3. isVerify()，参数是=JwtToken=。返回值是这个 JwtToken 是否存在
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public class JwtUtil
{
    // 创建默认的秘钥和算法，供无参的构造方法使用
    private static final String defaultBase64EncodedSecretKey = "B*B^";
    private static final SignatureAlgorithm defaultSignatureAlgorithm = SignatureAlgorithm.HS256;

    public JwtUtil()
    {
        this(defaultBase64EncodedSecretKey, defaultSignatureAlgorithm);
    }

    private final String base64EncodedSecretKey;
    private final SignatureAlgorithm signatureAlgorithm;

    public JwtUtil(String secretKey, SignatureAlgorithm signatureAlgorithm)
    {
        this.base64EncodedSecretKey = Base64.encodeBase64String(secretKey.getBytes());
        this.signatureAlgorithm = signatureAlgorithm;
    }

    /**
     * 这里就是产生 jwt 字符串的地方
     * jwt 字符串包括三个部分
     * 1. header
     * -当前字符串的类型，一般都是“JWT”
     * -哪种算法加密，“HS256”或者其他的加密算法
     * 所以一般都是固定的，没有什么变化
     * 2. payload
     * 一般有四个最常见的标准字段（下面有）
     * iat：签发时间，也就是这个 jwt 什么时候生成的
     * jti：JWT 的唯一标识
     * iss：签发人，一般都是 username 或者 userId
     * exp：过期时间
     */
    public String encode(String issuer, long ttlMillis, Map<String, Object> claims)
    {
        // iss 签发人，ttlMillis 生存时间，claims 是指还想要在 jwt 中存储的一些非隐私信息
        if (claims == null)
        {
            claims = new HashMap<>();
        }

        long nowMillis = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                // 荷载部分
                .setClaims(claims)
                // 这个是 JWT 的唯一标识，一般设置成唯一的，这个方法可以生成唯一标识
                .setId(UUID.randomUUID().toString())
                //2. 签发时间
                .setIssuedAt(new Date(nowMillis))
                // 签发人，也就是 JWT 是给谁的（逻辑上一般都是 username 或者 userId）
                .setSubject(issuer)
                // 这个地方是生成 jwt 使用的算法和秘钥
                .signWith(signatureAlgorithm, base64EncodedSecretKey);

        if (ttlMillis >= 0)
        {
            long expMillis = nowMillis + ttlMillis;
            // 4. 过期时间，这个也是使用毫秒生成的，使用当前时间+前面传入的持续时间生成
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    // 相当于 encode 的方向，传入 jwtToken 生成对应的 username 和 password 等字段。Claim 就是一个 map
    // 也就是拿到荷载部分所有的键值对
    public Claims decode(String jwtToken)
    {
        // 得到 DefaultJwtParser
        return Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(base64EncodedSecretKey)
                // 设置需要解析的 jwt
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    // 判断 jwtToken 是否合法
    public boolean isVerify(String jwtToken)
    {
        // 这个是官方的校验规则，这里只写了一个”校验算法“，可以自己加
        Algorithm algorithm;

        switch (signatureAlgorithm)
        {
            case HS256:
                algorithm = Algorithm.HMAC256(Base64.decodeBase64(base64EncodedSecretKey));
                break;
            default:
                throw new RuntimeException("不支持该算法");
        }

        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(jwtToken);

        // 校验不通过会抛出异常
        // 判断合法的标准：1. 头部和荷载部分没有篡改过。2. 没有过期
        return true;
    }
}
