import {create} from "zustand";
import {persist} from "zustand/middleware";

/**
 * 配置文件
 * @param tightBorder true/false 是否全框体展示
 */
export const DEFAULT_CONFIG = {
    tightBorder: false,
}

export type ChatConfig = typeof DEFAULT_CONFIG;

export type ChatConfigStore = ChatConfig & {
    reset: () => void;
    update: (updater: (config: ChatConfig) => void) => void;
};

/**
 * create 函数创建了一个 ChatConfigStore 实例，并且通过 export 关键字导出了一个名为 useAppConfig 的常量
 * ChatConfigStore 可能是一个自定义的数据存储类，而 useAppConfig 是一个用于在 React 组件中访问和修改 ChatConfigStore 实例的自定义 Hook
 */
export const useAppConfig = create<ChatConfigStore>()(
    persist(
        (set, get) => ({
            ...DEFAULT_CONFIG,

            reset() {
                set(() => ({...DEFAULT_CONFIG}));
            },

            update(updater) {
                const config = {...get()};
                updater(config);
                set(() => config);
            },
        }),

        {
            name: "app-config",
            version: 2,

            migrate(persistedState, version) {
                if (version === 2) {
                    return persistedState as any;
                }

                return persistedState as ChatConfig;
            },
        },
    ),
);
