package com.myxh.chatgpt.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author MYXH
 * @date 2024/1/29
 * @description 模型对象
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
@Getter
@AllArgsConstructor
public enum ChatGPTModel
{
    /**
     * gpt-3.5-turbo
     */
    GPT_3_5_TURBO("gpt-3.5-turbo"),

    /**
     * 文生图
     */
    DALL_E_2("dall-e-2"),
    DALL_E_3("dall-e-3"),
    ;

    private final String code;
}
