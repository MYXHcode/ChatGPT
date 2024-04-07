package com.myxh.chatgpt.test;

import com.myxh.chatgpt.domain.security.service.JwtUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author MYXH
 * @date 2023/12/8
 * @description 单元测试
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public class ApiTest
{
    @Test
    public void testJwt()
    {
        JwtUtil util = new JwtUtil("MYXH", SignatureAlgorithm.HS256);

        // 以 MYXH 作为秘钥，以 HS256 加密
        Map<String, Object> map = new HashMap<>();
        map.put("username", "MYXH");
        map.put("password", "520.ILY!");
        map.put("age", 21);

        String jwtToken = util.encode("MYXH", 30000, map);

        util.decode(jwtToken).forEach((key, value) -> System.out.println(key + ": " + value));
    }

    /**
     * 这是一个简单的测试，后续会开发 ChatGPT API
     * 测试时候，需要先获得授权 token
     * 获取方式：http://api.xfg.im:8080/authorize?username=xfg&password=123 - 此地址暂时有效，后续根据课程首页说明获取 token：https://t.zsxq.com/0d3o5FKvc
     * <p>
     * 2024 年 1 月更新
     * text-davinci-003 已失效不能使用，见下面新的测试方法 testChatGPT_3_5
     */
    @Test
    public void testChatGPT() throws IOException
    {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 用获取的 token 替换，默认有效期60分钟。地址非长期有效，只做学习验证。
        HttpPost post = new HttpPost("https://api.xfg.im/b8b6/v1/completions?token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4ZmciLCJleHAiOjE2ODMyMDQzMzYsImlhdCI6MTY4MzIwMDczNiwianRpIjoiYjM2Njg3ZjgtOWM5Yi00NzE1LWI2ZjctYjM0YmEyNzE2MWE3IiwidXNlcm5hbWUiOiJ4ZmcifQ.qBskmFVqx_0CKXdhtuSpqWn6XqB5Qq1Qu-c6_4-UoDg");

        post.addHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Bearer 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");

        String paramJson = "{\"model\": \"text-davinci-003\", \"prompt\": \"帮我写一个 java 冒泡排序\", \"temperature\": 0, \"max_tokens\": 1024}";

        StringEntity stringEntity = new StringEntity(paramJson, ContentType.create("text/json", "UTF-8"));
        post.setEntity(stringEntity);

        System.out.println("请求入参：" + paramJson);
        CloseableHttpResponse response = httpClient.execute(post);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            String res = EntityUtils.toString(response.getEntity());
            // api.xfg.im:443 requested authentication 表示 token 错误或者过期。
            System.out.println("测试结果：" + res);
        }
        else
        {
            System.out.println(response.getStatusLine().getStatusCode());
        }
    }

    /**
     * 因为官网模型更新，大家测试的时候使用 testChatGPT_3_5 方法
     */
    @Test
    public void testChatGPT_3_5() throws InterruptedException
    {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try
        {
            // 如果你有自己的 apihost、apikey 也可以替换使用
            HttpPost httpPost = new HttpPost("https://service-d6wuqy4n-1320869466.cd.apigw.tencentcs.com/v1/chat/completions");
            String json = "{\n" +
                    "    \"model\": \"gpt-3.5-turbo-1106\",\n" +
                    "    \"max_tokens\": 1024,\n" +
                    "    \"messages\": [\n" +
                    "        {\n" +
                    "            \"role\": \"user\",\n" +
                    "            \"content\": [\n" +
                    "                {\n" +
                    "                    \"text\": \"写个 java 冒泡排序\",\n" +
                    "                    \"type\": \"text\"\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);

            httpPost.setEntity(requestEntity);
            httpPost.setHeader("Authorization", "Bearer 阅读链接评论置顶第一条获取key https://t.zsxq.com/163o5FKvc");
            httpPost.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null)
            {
                // 打印响应体的内容
                String result = EntityUtils.toString(responseEntity);
                System.out.println(result);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                httpClient.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            countDownLatch.countDown();
        }

        // 等待
        countDownLatch.await();
    }
}
