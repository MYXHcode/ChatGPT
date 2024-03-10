import React from "react";
import "./styles/globals.scss";
import {Inter} from 'next/font/google'
import IcpFooter from './components/footer/icp-footer';

const inter = Inter({subsets: ['latin']})

export const metadata = {
    title: 'ChatGPT - MYXH',
    description: '您的 ChatGPT 贴心助手！',
}

export default function RootLayout({
                                       children,
                                   }: {
    children: React.ReactNode
}) {
    return (
        <html lang="en">
        <head>
            <script
                dangerouslySetInnerHTML={{
                    __html: `
              var _hmt = _hmt || [];
              (function() {
                var hm = document.createElement("script");
                hm.src = "https://hm.baidu.com/hm.js?22a70043116065a4220a252a4d74e6e4";
                var s = document.getElementsByTagName("script")[0]; 
                s.parentNode.insertBefore(hm, s);
              })();
            `,
                }}
            />
            <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
            <title></title>
        </head>

        <body className={inter.className}>
        {children}
        <>
            {/* 在这里添加 IcpFooter 组件 */}
            <IcpFooter/>
        </>
        </body>
        </html>
    )
}
