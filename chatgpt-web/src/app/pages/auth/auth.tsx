import {Button, Input} from "antd";
import styles from "./auth.module.scss";

import {useNavigate} from "react-router-dom";
import {useAccessStore} from "../../store/access";
import ChatGPTIcon from "../../icons/chatgpt.svg";

import Image from "next/image";

export function Auth() {
    const navigate = useNavigate();
    const access = useAccessStore();

    return (
        <div className={styles["auth-page"]}>
            <ChatGPTIcon/>
            <div className={styles["auth-title"]}>AI问答助手 By MYXH</div>

            <div className={styles["auth-sub-title"]}>
                学习 AI 开发、掌握 AI 部署、运用 AI 提效
            </div>

            <Image src={"/qrcode.png"} alt={"微信公众号二维码"} width={250} height={250}/>

            <div className={styles["auth-tips"]}>
                扫码关注公众号【AI问答助手 By MYXH】，
                <a
                    href="/qrcode.png"
                    target="_blank"
                >
                    回复【403】获取访问密码
                </a>
            </div>

            <Input
                className={styles["auth-input"]}
                type="text"
                placeholder="在此处填写访问码"
                value={access.accessCode}

                onChange={(e) => {
                    access.updateCode(e.currentTarget.value);
                }}

                status={access.accessCodeErrorMsgs ? 'error' : ''}
            />
            {access.accessCodeErrorMsgs ?
                <span className={styles['auth-error']}>{access.accessCodeErrorMsgs}</span> : null}

            <div className={styles["auth-actions"]}>
                <Button type="primary" onClick={() => access.login()}>确认登录👣</Button>
                <Button type="text"
                        onClick={() => window.open('https://github.com/MYXHcode')}>作者 GitHub 首页</Button>
            </div>

            <span className={styles["break-line"]}>
                说明：此平台是一个用于学习 OpenAI 项目开发的演示网站，不提供 OpenAI 的在线服务。这里的所有操作都是为了项目验证和学习编程技术。
                <br/>
                平台的主要目的是学习和研究 ChatGPT 和 ChatGLM。请确保您在使用相关资料时遵循合理、合法和合规的原则。
      </span>
        </div>
    );
}
