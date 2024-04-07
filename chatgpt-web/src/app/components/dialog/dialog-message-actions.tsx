import {ClearOutlined} from '@ant-design/icons';
import styles from '@/app/components/dialog/dialog-message-action.module.scss';
import {Select} from 'antd'
import {userChatStore} from '@/app/store/chat-store';
import {GptVersion} from '../../constants'
import {SessionConfig} from "@/types/chat";
import {CSSProperties, useRef, useState} from 'react';

export function Action(props: {
    icon: JSX.Element;
    onClick?: () => void;
    styles?: CSSProperties
}) {
    const {styles: sty} = props

    return <div className={styles['chat-input-action']} onClick={props.onClick}>
        <div className={styles["icon"]}>
            {props.icon}
        </div>
    </div>
}

export function ChatAction(props: {
    text?: string;
    icon: JSX.Element;
    onClick: () => void;
}) {
    const iconRef = useRef<HTMLDivElement>(null);
    const textRef = useRef<HTMLDivElement>(null);

    const [width, setWidth] = useState({
        full: 16,
        icon: 16,
    });

    function updateWidth() {
        if (!iconRef.current || !textRef.current) {
            return;
        }

        const getWidth = (dom: HTMLDivElement) => dom.getBoundingClientRect().width;
        const textWidth = getWidth(textRef.current);
        const iconWidth = getWidth(iconRef.current);

        setWidth({
            full: textWidth + iconWidth,
            icon: iconWidth,
        });
    }

    return (
        <div
            className={`${styles["chat-input-action"]} clickable`}

            onClick={() => {
                props.onClick();
                setTimeout(updateWidth, 1);
            }}

            onMouseEnter={updateWidth}
            onTouchStart={updateWidth}

            style={
                {
                    "--icon-width": `${width.icon}px`,
                    "--full-width": `${width.full}px`,
                } as React.CSSProperties
            }
        >
            <div ref={iconRef} className={styles["icon"]}>
                {props.icon}
            </div>

            <div className={styles["text"]} ref={textRef}>
                {props.text}
            </div>
        </div>
    );
}

export default function DialogMessagesActions(props: {
    config: SessionConfig
}) {
    const chatStore = userChatStore();
    const {config} = props

    return <div className={styles['chat-input-actions']}>
        <Select
            value={config?.gptVersion ?? GptVersion.CHATGLM_TURBO}
            style={{width: 160}}

            options={[
                {value: GptVersion.GPT_3_5_TURBO, label: 'chatgpt-3.5-turbo'},
                {value: GptVersion.GPT_3_5_TURBO_16K, label: 'chatgpt-3.5-turbo-16k'},
                {value: GptVersion.CHATGLM_LITE, label: 'chatglm_lite'},
                {value: GptVersion.CHATGLM_STD, label: 'chatglm_std'},
                {value: GptVersion.CHATGLM_PRO, label: 'chatglm_pro'},
                {value: GptVersion.CHATGLM_TURBO, label: 'chatglm_turbo'},
                {value: GptVersion.GPT_4, label: 'chatgpt-4【暂无】'},
                {value: GptVersion.GPT_4_32K, label: 'chatgpt-4-32k【暂无】'},
                {value: GptVersion.DALL_E_3, label: 'dall-e-3 画图'},
                // {value: GptVersion.DALL_E_2, label: 'dall-e-2 画图)'},
                // {value: GptVersion.CHATGLM_6B_SSE, label: 'chatglm_6b_sse'},
            ]}

            onChange={(value) => {
                chatStore.updateCurrentSession((session) => {
                    session.config = {
                        ...session.config,
                        gptVersion: value
                    }
                });
            }}
        />

        <ChatAction text="清除聊天" icon={<ClearOutlined/>} onClick={() => {
            chatStore.updateCurrentSession((session) => {
                if (session.clearContextIndex === session.messages.length) {
                    session.clearContextIndex = undefined;
                } else {
                    session.clearContextIndex = session.messages.length;
                }
            });
        }}/>
    </div>
}
