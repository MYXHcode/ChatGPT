import {GptVersion} from "@/app/constants";
import {useAccessStore} from "@/app/store/access";
import {MessageRole} from "@/types/chat";

// const host = 'https://964ef008-0367-4971-9569-963cd8bc6887.mock.pstmn.io'

const apiHostUrl = "http://localhost:8090";
// const apiHostUrl = "http://43.143.161.125:8090";
// const apiHostUrl = "http://myxh-chatqpt.site:8090";
// const apiHostUrl = "https://api.myxh-chatqpt.site";

// 构建前把 localhost 修改为你的公网 IP 或者域名地址
// const {apiHostUrl} = getServerSideConfig();

/**
 * Header 信息
 */
function getHeaders() {
    const accessState = useAccessStore.getState()

    const headers = {
        Authorization: accessState.token,
        'Content-Type': 'application/json;charset=utf-8'
    }

    return headers
}

/**
 * Role 角色获取接口
 */
export const getRoleList = () => {
    // 从 apiPost mock 接口获取
    /*
    return fetch(`${host}/role/list`).then((res) =>
        res.json()
    );
     */

    // 从本地 json 文件获取
    return fetch(`/prompts.json`).then((res) => res.json());
};

/**
 * 流式应答接口
 * @param data
 */
export const completions = (data: {
    messages: { content: string; role: MessageRole }[],
    model: GptVersion
}) => {
    return fetch(`${apiHostUrl}/api/v1/chatgpt/chat/completions`, {
        method: 'post',
        headers: getHeaders(),
        body: JSON.stringify(data)
    });
};

/**
 * 登录鉴权接口
 * @param token
 */
export const login = (token: string) => {
    const accessState = useAccessStore.getState()

    return fetch(`${apiHostUrl}/api/v1/auth/login`, {
        method: 'post',

        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },

        body: `code=${accessState.accessCode}`
    });
};

/**
 * 商品列表查询
 */
export const queryProductList = () => {
    return fetch(`${apiHostUrl}/api/v1/sale/query_product_list`, {
        method: "get",
        headers: getHeaders(),
    });
}

/**
 * 用户商品下单，获得支付地址 url
 */
export const createPayOrder = (productId: number) => {
    return fetch(`${apiHostUrl}/api/v1/sale/create_pay_order`, {
        method: "post",

        headers: {
            ...getHeaders(),
            "Content-Type": "application/x-www-form-urlencoded;charset=utf-8"
        },

        body: `productId=${productId}`
    });
}
