import {useState} from "react";
import styles from './dialog-message-input.module.scss';
import {Button, Input} from "antd";
import {userChatStore} from "@/app/store/chat-store";
import DialogMessagesActions from "./dialog-message-actions";

interface Props {
    onEnter: (value: any) => void;
}

/**
 * 对话消息输入
 * @constructor
 */
export function DialogMessageInput(props: Props) {
    const {onEnter} = props;
    const chatStore = userChatStore();
    const [value, setValue] = useState<string>();
    const currentSession = chatStore.currentSession();

    const onSend = (value: any) => {
        // 输入内容
        onEnter(value);

        // 清空当前对话框
        setValue(undefined);
    }

    const handleKeyDown = (e: any) => {
        if (e.ctrlKey && e.key === "Enter") {
            onSend(value);
        }
    }

    return (
        <div className={styles.wrapper}>
            <DialogMessagesActions config={currentSession.config}/>

            <Input.TextArea
                value={value}
                onChange={(e) => setValue(e.target.value)}
                className={styles.textarea}
                placeholder={"请输入"}
                autoFocus
                onKeyDown={handleKeyDown}/>

            <Button disabled={!value?.length} type="primary" className={styles.btn}
                    onClick={() => onSend(value)}>发送(Ctrl+Enter)
            </Button>
        </div>
    );
}
