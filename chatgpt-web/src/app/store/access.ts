import { create } from "zustand";
import { persist } from "zustand/middleware";
import { login } from "@/apis";

export interface AccessControlStore {
    accessCode: string;
    token: string;
    accessCodeErrorMsgs: string;

    updateToken: (_: string) => void;
    updateCode: (_: string) => void;
    isAuthorized: () => boolean;
    login: () => Promise<string>;
    goToLogin: () => void;
}

export const useAccessStore: any = create<AccessControlStore>()(
    persist(
        (set, get) => ({
            token: "",
            accessCode: "",
            accessCodeErrorMsgs: "",

            updateCode(code: string) {
                set(() => ({ accessCode: code }));
            },

            updateToken(token: string) {
                set(() => ({ token }));
            },

            isAuthorized() {
                return !!get().token;
            },

            goToLogin() {
                get().updateCode("");
                get().updateToken("");
            },

            async login() {
                const res = await login(get().accessCode);
                const { data, code } = await res.json();
                console.log("data", data);

                // 这里需要根据返回结果设置
                if (code === "0000") {
                    console.log("登陆成功");
                    get().updateToken(data);
                    set(() => ({ accessCodeErrorMsgs: "" }));
                }

                if (code === "0002") {
                    set(() => ({ accessCodeErrorMsgs: "验证码已过期,请获取最新验证码" }));
                }

                if (code === "0003") {
                    set(() => ({ accessCodeErrorMsgs: "验证码不存在,请确认最新验证码" }));
                }

                return data;
            },
        }),

        {
            name: "chat-access",
            version: 1,
        }
    )
);
