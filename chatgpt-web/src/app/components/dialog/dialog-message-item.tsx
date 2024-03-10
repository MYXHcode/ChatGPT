import styles from './dialog-message-item.module.scss'
import {Avatar, Space} from "antd";
import {Message, MessageRole} from "@/types/chat";
import {RefObject} from 'react';
import {Markdown} from '@/app/components/markdown/markdown';
import {CopyOutlined, DeleteOutlined, SyncOutlined} from '@ant-design/icons'
import {ChatAction} from './dialog-message-actions'
import dayjs from 'dayjs'
import {userChatStore} from '@/app/store/chat-store';
import {copyToClipboard} from '@/utils'

/**
 * 用对象封装属性，方便扩展
 */
interface Props {
    message: Message;
    parentRef?: RefObject<HTMLDivElement>;
}

/**
 * 对话面板消息元素
 * @constructor
 */
export function DialogMessageItem(props: Props) {
    const {message, parentRef} = props;
    const chatStore = userChatStore();
    const isUser = message.role === MessageRole.user;
    const date = message?.time ? dayjs(message.time).format('YYYY/MM/DD HH:mm:ss') : ''

    const retryHandle = () => {
        chatStore.onRetry()
    }

    const copyHandle = async () => {
        await copyToClipboard(message.content)
    }

    const deleteHandle = async () => {
        chatStore.deleteMessage(message)
    }

    return <>
        <div
            className={
                isUser ? styles["chat-message-user"] : styles["chat-message"]
            }
        >
            <div className={styles["chat-message-container"]}>
                <div className={styles["chat-message-header"]}>
                    <div className={styles["chat-message-avatar"]}>
                        <Avatar shape="square" src={message.avatar} size={30} style={{
                            borderRadius: '4px',
                            backgroundColor: '#f6f6f6'
                        }}/>
                    </div>

                    <div className={styles['chat-message-edit']}>
                        <Space>
                            <ChatAction icon={<SyncOutlined/>} text="重试" onClick={retryHandle}/>
                            <ChatAction icon={<CopyOutlined/>} text="复制" onClick={copyHandle}/>
                            <ChatAction icon={<DeleteOutlined/>} text="删除" onClick={deleteHandle}/>
                        </Space>
                    </div>
                </div>

                <div className={styles["chat-message-item"]}>
                    <Markdown
                        content={message.content}
                        fontSize={14}
                        parentRef={parentRef}
                        defaultShow={false}

                        loading={
                            (message.content.length === 0) &&
                            !isUser
                        }
                    />
                </div>

                <div className={styles['date']}>{date}</div>
            </div>
        </div>
    </>
}
