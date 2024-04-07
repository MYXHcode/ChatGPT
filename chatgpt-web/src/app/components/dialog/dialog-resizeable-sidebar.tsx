import {PropsWithChildren} from "react";
import {Resizable} from "re-resizable";

interface Props {
    minWidth?: number;
}

export function DialogResizeableSidebar(props: PropsWithChildren<Props>) {
    const {minWidth = 200, children} = props;
    return (
        <Resizable
            minWidth={220}
            maxWidth={320}

            defaultSize={{
                width: "100%",
                height: "100%",
            }}

            style={{
                borderRight: '1px solid #f5f5f5'
            }}
        >
            {children}
        </Resizable>
    );
}
