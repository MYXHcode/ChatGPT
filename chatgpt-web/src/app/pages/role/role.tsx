import styles from './role.module.scss'
import {useEffect, useState} from "react";
import {Role} from "@/types/role"
import {getRoleList} from "@/apis";
import {RoleContext, RoleList} from "@/app/components/role/role-list";
import {Outlet} from "react-router-dom";

export function Role() {
    const [roles, setRoles] = useState<Role[]>([])
    const [selected, setSelected] = useState<number>(-1);

    useEffect(() => {
        getRoleList().then((res) => {
            setRoles(res?.roles);
        });
    }, [])

    return (
        <div className={styles["role"]}>
            <RoleContext.Provider value={{roles, selected, setSelected}}>
                <RoleList/>

                {/* 在父级路由中定义一个占位符 */}
                <Outlet/>
            </RoleContext.Provider>
        </div>
    );
}
