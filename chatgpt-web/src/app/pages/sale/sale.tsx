import styles from './sale.module.scss';
import QRCode from 'qrcode.react';
import {createPayOrder, queryProductList} from "@/apis";
import {useEffect, useState} from "react";
import {SaleProduct, SaleProductEnum} from "@/types/sale_product";
import {useAccessStore} from "@/app/store/access";
import {useNavigate} from "react-router-dom";

export function Sale() {
    const [products, setProducts] = useState<SaleProduct[]>([])
    const [showModal, setShowModal] = useState(false);
    const [payUrl, setPayUrl] = useState('')

    // 编程式路由跳转
    const navigate = useNavigate();

    const handleButtonClick = () => {
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
    };

    const queryProductListHandle = async () => {
        const res = await queryProductList();
        const {data, code} = await res.json();

        // 登录拦截
        if (code === SaleProductEnum.NeedLogin) {
            useAccessStore.getState().goToLogin();
        }

        // 设置结果
        setProducts(data);
    }

    const payOrder = async (productId: number) => {
        const res = await createPayOrder(productId);
        const {data, code} = await res.json();

        // 登录拦截
        if (code === SaleProductEnum.NeedLogin) {
            useAccessStore.getState().goToLogin();
        }

        // 支付唤起
        if (code == SaleProductEnum.SUCCESS) {
            setPayUrl(data);
            handleButtonClick();
        }
    }

    useEffect(() => {
        queryProductListHandle().then(r => {
        });
    }, [])

    return (
        <div className={styles["sale"]}>
            {products?.map((product) => (
                <div key={product.productId} className={styles["product"]}>
                    <div className={styles["product-name"]}>
                        {product.productName}
                    </div>

                    <div className={styles["product-token"]}>
                        {product.quota}<span className={styles["product-token-subscript"]}>(条)</span>
                    </div>

                    <div className={styles["product-price"]}>
                        <span style={{color: '#af0000', fontSize: "20px"}}>￥{product.price.toFixed(2)}</span>
                    </div>

                    <div className={styles["product-buy"]} onClick={() => payOrder(product.productId)}>
                        立即购买
                    </div>

                    <div className={styles["product-desc"]}>
                        <span>{product.productDesc}</span>
                    </div>
                </div>
            ))}

            {showModal && (
                <div className={styles["product-pay"]}>
                    <div className={styles["product-pay-weixin"]}>
                        微信扫码支付
                    </div>

                    <div className={styles["product-pay-url"]}>
                        <QRCode value={payUrl}/>
                    </div>

                    <div className={styles["product-pay-close"]}>
                        <div onClick={handleCloseModal}>😁 支付完成，点击关闭。去对话</div>
                    </div>

                    <div className={styles["product-pay-prompt"]}>
                        <span>支付成功，自动充值。可直接去<span style={{color: "rgb(0,0,0)", fontWeight: "bold"}}
                                                              onClick={() => {
                                                                  navigate(`/chat`);
                                                              }}>【对话】</span>使用</span>
                    </div>
                </div>
            )}
        </div>
    )
}
