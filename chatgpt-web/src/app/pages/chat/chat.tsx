import styles from './chat.module.scss';
import {DialogList} from "@/app/components/dialog/dialog-list";
import {Outlet} from 'react-router-dom';

export function Chat() {
    return (
        <div className={styles["chat"]}>
            <DialogList/>
            <Outlet/>
        </div>
    );
}
