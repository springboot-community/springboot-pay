package io.springboot.pay.api;

import javax.servlet.http.HttpServletRequest;

public interface IAliPay {
    /**
     * @param orderId
     * @return
     * @link https://docs.open.alipay.com/api_1/alipay.trade.page.pay/
     */
    String doAliPay(String orderId);

    /**
     * 支付宝回调
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    Object aliPaycallBack(HttpServletRequest request, HttpServletRequest response) throws Exception;

    /**
     * 支付宝通知
     *
     * @param request
     * @param response
     * @throws Exception
     */
    void aliPayNotify(HttpServletRequest request, HttpServletRequest response) throws Exception;
}
