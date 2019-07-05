package io.springboot.pay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.springboot.pay.service.AliPayService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author 王小明
 * @Description 支付流程三步走
 * @First 创建订单 ---> 返回订单信息
 * @Second 立即支付 ---> 返回支付二维码
 * @Finally 手机扫码支付 ---> 完事
 */
@RestController
@RequestMapping(value = "alipay")
public class AliPayController {

    @Autowired
    private AliPayService aliPayService;

    private static final Logger logger = LoggerFactory.getLogger(AliPayController.class);

    /**
     * @param orderId 订单mongo ObjectId
     * @return
     * @link https://docs.open.alipay.com/api_1/alipay.trade.page.pay/
     * @Description 开始支付操作 最终跳转至二维码页面
     */
    @RequestMapping("doalipay.php")
    public Object doAliPay(String orderId) {
        String result = aliPayService.doAliPay(orderId);
        logger.info("这里的result是个form表单html字符串 ：{}", result);
        return result;
    }

    /**
     * 回调方法
     * 通常支付成功后需要返回到相应的页面
     * 在用户执行doalipay.php后将获取支付宝二维码支付页面
     * 待用户扫码支付完毕将由支付宝重定向到此action上
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("callback.php")
    public Object aliPaycallBack(HttpServletRequest request, HttpServletRequest response) throws Exception {
        return aliPayService.aliPaycallBack(request, response);
    }

    /**
     * 通知方法 沙箱可能是阉割版？ 订单关闭超时关闭居然不通知？
     * 通常支付宝会将订单的信息再次传输到这个通知action 上可以做一些记录
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("notify.php")
    public void aliPayNotify(HttpServletRequest request, HttpServletRequest response) throws Exception {
        this.aliPayService.aliPayNotify(request, response);
    }

    /**
     * 根据接口查询账单状态
     * 目前不知道这个接口的使用价值，除了能查出残缺的支付宝账号外毫无作用？ 可能是沙箱版的原因？
     * 商户订单号生成由我们自己，可是支付宝订单号由支付宝生成当用户扫码的时候
     * 可是这个时候支付宝居然不发通知，我们无法获取支付宝订单号也就无法使用这个
     * 接口的来查询这笔订单的情况。
     *
     * @param outTradeNo 商户订单号
     * @param tradeNo   支付宝订单号
     * @return
     * @throws Exception
     * @link https://docs.open.alipay.com/api_1/alipay.trade.query/
     */
    @RequestMapping("getbillinfo.php")
    public Object getBillInfo(String outTradeNo, String tradeNo) throws Exception {
        return aliPayService.getBillInfo(outTradeNo, tradeNo);
    }

    /**
     * 交易退款
     *
     * @param outTradeNo 商户订单号
     * @param tradeNo   支付宝订单号
     * @param refundFee 退款金额 ：圆.角分
     * @param refundReason 退款原因 ：瞎鸡儿写完事
     * @return
     * @throws Exception
     * @link https://docs.open.alipay.com/api_1/alipay.trade.refund/
     */
    @RequestMapping("refund.php")
    public Object alipayTradeRefund(String outTradeNo, String tradeNo, double refundFee, String refundReason) throws Exception {
        return aliPayService.alipayTradeRefund(outTradeNo, tradeNo, refundFee, refundReason);
    }
}
