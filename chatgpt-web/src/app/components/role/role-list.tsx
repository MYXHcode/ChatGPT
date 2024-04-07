import styles from "./role-list.module.scss";
import {useNavigate} from "react-router-dom";
import {useContext} from "react";
import React from "react";
import {Role} from '@/types/role';
import {DialogResizeableSidebar} from "@/app/components/dialog/dialog-resizeable-sidebar";
import {Avatar, Spin} from "antd";
import {DialogHead} from "@/app/components/dialog/dialog-head";

export interface RoleContextType {
    roles: Role[]
    selected: number;
    setSelected: (id: number) => void;
}

export const RoleContext = React.createContext<RoleContextType>({
    roles: [],
    selected: -1,

    setSelected: (id: number) => {
    }
})

export function RoleList() {
    // 编程式路由跳转
    const navigate = useNavigate();
    const {roles, selected, setSelected} = useContext(RoleContext);

    return (
        <DialogResizeableSidebar>
            {/* 头部操作 */}
            <DialogHead/>

            {/* 角色列表 */}
            <div className={styles["role-list"]}>
                {!roles ? <Spin spinning style={{margin: '24px auto', width: '100%'}}/> : null}

                {roles?.map((role) => (
                    <div
                        className={`${styles["role-item"]} ${selected == role.id ? styles['selected'] : ''}`}
                        key={role.id}

                        onClick={() => {
                            setSelected(role.id)
                            navigate(`/role/${role.id}`);
                        }}>

                        <Avatar shape="square" size={38} src={role.avatar}/>

                        <div className={styles["name"]}>{role.role_name}</div>
                    </div>
                ))}
            </div>
        </DialogResizeableSidebar>
    );
}
