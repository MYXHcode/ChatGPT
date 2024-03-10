package com.myxh.chatgpt.interfaces;

import com.myxh.chatgpt.domain.security.service.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MYXH
 * @date 2023/12/9
 * @description API 访问准入管理；当访问 OpenAI 接口时，需要进行准入验证。
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@RestController
public class ApiAccessController
{
    private final Logger logger = LoggerFactory.getLogger(ApiAccessController.class);

    /**
     * http://localhost:8080/authorize?username=MYXH&password=520.ILY!
     * http://43.143.161.125:8080/authorize?username=MYXH&password=520.ILY!
     */
    @RequestMapping("/authorize")
    public ResponseEntity<Map<String, String>> authorize(String username, String password)
    {
        Map<String, String> map = new HashMap<>();

        // 模拟账号和密码校验
        if (!"MYXH".equals(username) || !"520.ILY!".equals(password))
        {
            map.put("msg", "用户名密码错误");

            return ResponseEntity.ok(map);
        }

        // 校验通过生成 token
        JwtUtil jwtUtil = new JwtUtil();
        Map<String, Object> chaim = new HashMap<>();
        chaim.put("username", username);
        String jwtToken = jwtUtil.encode(username, 5 * 60 * 1000, chaim);
        map.put("msg", "授权成功");
        map.put("token", jwtToken);

        // 返回 token 码
        return ResponseEntity.ok(map);
    }

    /**
     * http://localhost:8080/verify?token=
     * http://43.143.161.125:8080/verify?token=
     */
    @RequestMapping("/verify")
    public ResponseEntity<String> verify(String token)
    {
        logger.info("验证 token：{}", token);

        return ResponseEntity.status(HttpStatus.OK).body("verify success!");
    }

    @RequestMapping("/success")
    public String success()
    {
        return "test success by MYXH";
    }
}
