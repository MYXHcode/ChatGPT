import React from 'react';
import styles from "./icp-footer.module.scss";

const IcpFooter: React.FC = () => {
    return (
        <div className={styles["footer-bottom"]}>
            <p>
                <a href="http://beian.miit.gov.cn" target="_blank" rel="noopener noreferrer">青 ICP 备 2024001544
                    号-1</a> | 遵循<a href="https://www.gnu.org/licenses/gpl-3.0.html" target="_blank"
                                      rel="noopener noreferrer"> GNU 通用公共许可证 Version 3</a> | 版权所有 © 2024 <a
                href="https://github.com/MYXHcode" target="_blank" rel="noopener noreferrer">末影小黑xh</a>，保留所有权利。
            </p>
        </div>
    );
};

export default IcpFooter;
