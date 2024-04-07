package com.myxh.chatgpt.interfaces;

import com.myxh.chatglm.model.ChatCompletionRequest;
import com.myxh.chatglm.model.Model;
import com.myxh.chatglm.model.Role;
import com.myxh.chatglm.session.Configuration;
import com.myxh.chatglm.session.OpenAiSession;
import com.myxh.chatglm.session.OpenAiSessionFactory;
import com.myxh.chatglm.session.defaults.DefaultOpenAiSessionFactory;
import com.myxh.chatgpt.application.IWeiXinValidateService;
import com.myxh.chatgpt.domain.receive.model.MessageTextEntity;
import com.myxh.chatgpt.infrastructure.util.XmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author MYXH
 * @date 2023/12/13
 * @description 微信公众号，请求处理服务
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@RestController
@RequestMapping("/wx/portal/{appid}")
public class WeiXinPortalController
{
    private final Logger logger = LoggerFactory.getLogger(WeiXinPortalController.class);

    @Value("${wx.config.originalid}")
    private String originalId;

    @Resource
    private IWeiXinValidateService weiXinValidateService;

    private final OpenAiSession openAiSession;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    // 存放 OpenAi 返回结果数据
    private final Map<String, String> openAiDataMap = new ConcurrentHashMap<>();

    // 存放 OpenAi 调用次数数据
    private final Map<String, Integer> openAiRetryCountMap = new ConcurrentHashMap<>();

    public WeiXinPortalController()
    {
        // 1. 配置文件；智谱 Ai 申请 ApiSecretKey
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("2abbbc41b164c2f21740e82582ed44b5.YQcsJ0j4CMwuLVfL");

        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 3. 开启会话
        this.openAiSession = factory.openSession();
        logger.info("开始 openAiSession");
    }

    /**
     * 处理微信服务器发来的 get 请求，进行签名的验证【付费的 natapp 对接稳定，3 块钱域名，9 块钱渠道费】
     * http://myxh-chatqpt.nat300.top/wx/portal/wxa175a0b560bd00a2
     * <p>
     * appid     微信端 AppID
     * signature 微信端发来的签名
     * timestamp 微信端发来的时间戳
     * nonce     微信端发来的随机字符串
     * echostr   微信端发来的验证字符串
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String validate(@PathVariable String appid,
                           @RequestParam(value = "signature", required = false) String signature,
                           @RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "echostr", required = false) String echostr)
    {
        try
        {
            logger.info("微信公众号验签信息{}开始 [{}, {}, {}, {}]", appid, signature, timestamp, nonce, echostr);

            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr))
            {
                throw new IllegalArgumentException("请求参数非法，请核实!");
            }

            boolean check = weiXinValidateService.checkSign(signature, timestamp, nonce);
            logger.info("微信公众号验签信息{}完成 check：{}", appid, check);

            if (!check)
            {
                return null;
            }

            return echostr;
        }
        catch (Exception e)
        {
            logger.error("微信公众号验签信息{}失败 [{}, {}, {}, {}]", appid, signature, timestamp, nonce, echostr, e);

            return null;
        }
    }

    /**
     * 此处是处理微信服务器的消息转发的
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String appid,
                       @RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature)
    {
        try
        {
            logger.info("接收微信公众号信息请求{}开始 {}", openid, requestBody);
            MessageTextEntity message = XmlUtil.xmlToBean(requestBody, MessageTextEntity.class);
            logger.info("请求次数：{}", null == openAiRetryCountMap.get(message.getContent().trim()) ? 1 : openAiRetryCountMap.get(message.getContent().trim()));

            // 异步任务【加入超时重试，对于小体量的调用反馈，可以在重试有效次数内返回结果】
            if (openAiDataMap.get(message.getContent().trim()) == null || "NULL".equals(openAiDataMap.get(message.getContent().trim())))
            {
                String data = "消息处理中，请再回复我一句【" + message.getContent().trim() + "】";

                // 休眠等待
                Integer retryCount = openAiRetryCountMap.get(message.getContent().trim());

                if (null == retryCount)
                {
                    if (openAiDataMap.get(message.getContent().trim()) == null)
                    {
                        doChatGPTTask(message.getContent().trim());
                    }

                    logger.info("超时重试：{}", 1);
                    openAiRetryCountMap.put(message.getContent().trim(), 1);
                    TimeUnit.SECONDS.sleep(5);
                    new CountDownLatch(1).await();
                }
                else if (retryCount < 2)
                {
                    retryCount = retryCount + 1;
                    logger.info("超时重试：{}", retryCount);
                    openAiRetryCountMap.put(message.getContent().trim(), retryCount);
                    TimeUnit.SECONDS.sleep(5);
                    new CountDownLatch(1).await();
                }
                else
                {
                    retryCount = retryCount + 1;
                    logger.info("超时重试：{}", retryCount);
                    openAiRetryCountMap.put(message.getContent().trim(), retryCount);
                    TimeUnit.SECONDS.sleep(3);
                    if (openAiDataMap.get(message.getContent().trim()) != null && !"NULL".equals(openAiDataMap.get(message.getContent().trim())))
                    {
                        data = openAiDataMap.get(message.getContent().trim());
                    }
                }

                // 反馈信息[文本]
                MessageTextEntity res = new MessageTextEntity();
                res.setToUserName(openid);
                res.setFromUserName(originalId);
                res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
                res.setMsgType("text");
                res.setContent(data);

                return XmlUtil.beanToXml(res);
            }

            // 反馈信息[文本]
            MessageTextEntity res = new MessageTextEntity();
            res.setToUserName(openid);
            res.setFromUserName(originalId);
            res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
            res.setMsgType("text");
            res.setContent(openAiDataMap.get(message.getContent().trim()));
            String result = XmlUtil.beanToXml(res);
            logger.info("接收微信公众号信息请求{}完成 {}", openid, result);
            openAiDataMap.remove(message.getContent().trim());

            return result;
        }
        catch (Exception e)
        {
            logger.error("接收微信公众号信息请求{}失败 {}", openid, requestBody, e);
            return "";
        }
    }

    public void doChatGPTTask(String content)
    {
        openAiDataMap.put(content, "NULL");

        taskExecutor.execute(() -> {
            // 入参；模型、请求信息；记得更新最新版 ChatGLM-SDK-Java
            ChatCompletionRequest request = new ChatCompletionRequest();

            // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
            request.setModel(Model.CHATGLM_TURBO);

            request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>()
            {
                private static final long serialVersionUID = -7988151926241837899L;

                {
                    add(ChatCompletionRequest.Prompt.builder()
                            .role(Role.user.getCode())
                            .content(content)
                            .build());
                }
            });

            // 同步获取结果
            try
            {
                CompletableFuture<String> future = openAiSession.completions(request);
                openAiDataMap.put(content, future.get());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        });
    }
}
