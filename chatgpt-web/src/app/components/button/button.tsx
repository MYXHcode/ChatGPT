import styles from "./button.module.scss";

/**
 * 定义通用按钮函数 IconButton
 * @param props.onClick 按钮事件(可选)
 * @param props.icon 图标(可选)
 * @param props.className CSS 样式
 * @param props.title 图标名称
 * @param props.text 图标说明
 * */
export function IconButton(props: {
    onClick?: () => void;
    icon?: JSX.Element;
    className?: string;
    title?: string;
    text?: string;
    backgroundColor?: string;
}) {
    const {backgroundColor} = props;

    const buttonStyle = {
        backgroundColor: backgroundColor,
    };

    return (
        <button className={styles["icon-button"]} style={buttonStyle} onClick={props.onClick}>
            {props.icon && <div className={styles["icon-button-icon"]}>{props.icon}</div>}
            {props.text && <div className={styles["icon-button-text"]}>{props.text}</div>}
        </button>
    );
}
