docker run -p 8080:8080 --name chatgpt-api -d myxh/chatgpt-api:1.1

docker run -p 8080:8080 \
--name chatgpt-api \
-e PARAMS="
    --sever.port=8080
    --chatgpt.host=https://sdk.xfg.im/
    --chatgpt.apiKey=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
    --wx.config.originalid=gh_8aed07a01479
    --wx.config.appid=wxa175a0b560bd00a2
    --wx.config.gatewayAddress=MYXH" \
-d myxh/chatgpt-api
