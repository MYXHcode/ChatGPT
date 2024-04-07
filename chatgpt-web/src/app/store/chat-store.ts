import {create} from "zustand";
import {persist} from "zustand/middleware";
import {Dialog, Message, MessageDirection, MessageRole, MessageType, SessionConfig} from "@/types/chat";
import {GptVersion} from "@/app/constants";
import {nanoid} from "nanoid";
import {completions} from "@/apis";
import {useAccessStore} from "./access";

interface ChatStore {
    id: number;
    sessions: ChatSession[];
    currentSessionIndex: number;
    openSession: (dialog?: { avatar?: string; title?: string }) => ChatSession;
    selectSession: (index: number) => void;
    deleteSession: (index: number) => void;
    currentSession: () => ChatSession;
    onSendMessage: (newMessage: Message) => Promise<void>;
    updateCurrentSession: (updater: (session: ChatSession) => void) => void;
    onRetry: () => void;
    deleteMessage: (message: Message) => void;
    createNewMessage: (value: string) => Message;
}

export interface ChatSession {
    // 会话ID
    id: number;

    // 对话框体
    dialog: Dialog;

    // 对话消息
    messages: Message[];

    // 会话配置
    config: SessionConfig;

    // 清除会话的索引
    clearContextIndex?: number;
}

function createChatSession(dialog?: {
    avatar?: string;
    title?: string;
}): ChatSession {
    return {
        id: 0,

        dialog: {
            avatar: dialog?.avatar || "/role/wali.png",
            title: dialog?.title || "新的对话",
            count: 0,
            subTitle: "请问有什么需要帮助的吗？",
            timestamp: new Date().getTime(),
        },

        messages: [
            {
                avatar: dialog?.avatar || "/role/wali.png",
                content: "请问有什么需要帮助的吗？",
                message_type: MessageType.Text,
                time: Date.now(),
                direction: MessageDirection.Receive,
                role: MessageRole.system,
                id: nanoid()
            }
        ],

        clearContextIndex: undefined,

        config: {
            gptVersion: GptVersion.CHATGLM_TURBO,
        }
    };
}

function formatMessages(messages: Message[]) {
    // 如果历史消息超过 5，只取最新的 3 个
    // 获取最新的三个消息，如果 messages 长度小于等于 3，则返回全部消息
    const latestMessages = messages.length > 3 ? messages.slice(-3) : messages;

    return latestMessages.map(({content, role}) => ({
        content,
        role,
    }));
}

export function createNewMessage(value: string, role?: MessageRole) {
    return {
        avatar: role !== MessageRole.user ? "/role/wali.png" : "/role/OhtoAi.png",
        content: value,
        time: Date.now(),
        role: role || MessageRole.user,
        id: nanoid(),
        streaming: false,
    } as Message;
}

export const userChatStore = create<ChatStore>()(
    persist(
        (set, get) => ({
            // 属性赋值
            id: 0,
            sessions: [createChatSession()],
            currentSessionIndex: 0,

            // 开启会话
            openSession(dialog?: { avatar?: string; title?: string }) {
                const session = createChatSession(dialog);

                // 每开启一个会话，就对应设置一个对话 ID
                set(() => ({id: get().id + 1}));
                session.id = get().id;

                // 保存创建的会话，到 sessions 数组中
                set((state) => ({
                    currentSessionIndex: 0,

                    // 在数组头部插入数据
                    sessions: [session].concat(state.sessions),
                }));

                return session;
            },

            // 选择会话
            selectSession(index: number) {
                set({
                    currentSessionIndex: index,
                });
            },

            // 删除会话
            deleteSession(index: number) {
                const count = get().sessions.length;
                const deleteSession = get().sessions.at(index);

                if (!deleteSession) {
                    return;
                }

                const sessions = get().sessions.slice();
                sessions.splice(index, 1);

                const currentIndex = get().currentSessionIndex;

                let nextIndex = Math.min(
                    currentIndex - Number(index < currentIndex),
                    sessions.length - 1,
                );

                if (count === 1) {
                    nextIndex = 0;
                    sessions.push(createChatSession());
                }

                set(() => ({
                    currentSessionIndex: nextIndex,
                    sessions,
                }));
            },

            // 当前会话
            currentSession() {
                let index = get().currentSessionIndex;
                const sessions = get().sessions;

                if (index < 0 || index >= sessions.length) {
                    index = Math.min(sessions.length - 1, Math.max(0, index));
                    set(() => ({currentSessionIndex: index}));
                }

                return sessions[index];
            },

            // 发送消息
            async onSendMessage(newMessage: Message) {
                const session = get().currentSession();

                get().updateCurrentSession((session) => {
                    session.messages = session.messages.concat(newMessage);
                });

                const activeMessages = session.messages?.slice(
                    session.clearContextIndex || 0
                );

                const messages = formatMessages(activeMessages);

                const botMessage: Message = createNewMessage("", MessageRole.system);

                get().updateCurrentSession((session) => {
                    session.messages = session.messages.concat(botMessage);
                });

                // 调用接口
                const {body} = await completions({
                    messages,
                    model: session.config.gptVersion,
                });

                // 填充消息
                const reader = body!.getReader();
                const decoder = new TextDecoder();

                new ReadableStream({
                    start(controller) {
                        async function push() {
                            const {done, value} = await reader.read();
                            if (done) {
                                controller.close();

                                return;
                            }

                            controller.enqueue(value);
                            const text = decoder.decode(value);

                            // 权限校验
                            if (text === "0003") {
                                controller.close();
                                useAccessStore.getState().goToLogin();
                            }

                            botMessage.content += text;

                            get().updateCurrentSession((session) => {
                                session.messages = session.messages.concat();
                            });

                            await push();
                        }
                        push();
                    },
                });
            },

            // 更新当前会话
            updateCurrentSession(updater) {
                const sessions = get().sessions;
                const index = get().currentSessionIndex;
                updater(sessions[index]);
                set(() => ({sessions}))
            },

            onRetry() {
                const session = get().currentSession();
                const activeMessages = session.messages?.slice(session.clearContextIndex || 0);
                const messages = formatMessages(activeMessages);
                completions({messages, model: session.config.gptVersion});
            },

            deleteMessage(message: Message) {
                get().updateCurrentSession((session) => {
                    const index = session.messages.findIndex((m) => m.id === message.id);
                    session.messages.splice(index, 1);
                });
            },

            createNewMessage(value: string, role?: MessageRole) {
                return {
                    avatar: "/role/OhtoAi.png",
                    content: value,
                    time: Date.now(),
                    role: MessageRole.user,
                    id: nanoid(),
                } as Message;
            }
        }),

        {
            name: "chat-store"
        }
    ),
);
