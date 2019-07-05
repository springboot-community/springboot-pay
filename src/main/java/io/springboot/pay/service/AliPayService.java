package io.springboot.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import io.springboot.pay.api.AliPayStatus;
import io.springboot.pay.api.PayType;
import io.springboot.pay.config.AlipayConfig;
import io.springboot.pay.model.Order;
import io.springboot.pay.api.IAliPay;
import io.springboot.pay.model.Product;
import io.springboot.pay.util.ApiMessage;
import io.springboot.pay.util.ApiResponse;
import io.springboot.pay.util.GeneralUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliPayService implements IAliPay {
    @Value("${ali.pay.seller.id}")
    private String sellerId;
    @Value("${ali.pay.return.url:http://ojbkplus.vaiwan.com/alipay/callback.php}")
    private String returnUrl;
    @Value("${ali.pay.notify.url:http://ojbkplus.vaiwan.com/alipay/notify.php}")
    private String notifyUrl;

    @Resource(name = "alipayClient")
    private AlipayClient alipayClient;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private NoticeService noticeService;

    private static final Logger logger = LoggerFactory.getLogger(AliPayService.class);

    /**
     * @param orderId 订单mongo ObjectId
     * @return
     * @link 更多参数解释请查阅 https://docs.open.alipay.com/api_1/alipay.trade.page.pay/
     * @Description 开始支付操作 最终跳转至二维码页面
     */
    @Override
    public String doAliPay(String orderId) {
        logger.info("开始支付");
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        alipayTradePagePayRequest.setNotifyUrl(notifyUrl);
        alipayTradePagePayRequest.setReturnUrl(returnUrl);
        Order order = orderService.getOrderById(orderId);
        String result = "提示失败的html字符串可以往当前这个字符串里塞，如果下面的AlipayApiException 出现了页面就会显示我们自己的html而不是支付宝form表单了";
        if (order == null) {
            return result;
        }
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", order.getOutTradeNo());
        bizContent.put("total_amount", order.getTotalAmount());  //订单金额: 圆.角分
        bizContent.put("subject", order.getSubject());           //订单标题
        bizContent.put("seller_id", sellerId);                   //收钱的卖家UID
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");//固定值
        bizContent.put("body", order.getBody());                 //订单描述
        bizContent.put("qr_pay_mode", "2");                      //跳转模式
        //bizContent.put("time_expire","2019-07-05 11:40:01");   //订单绝对超时时间
        ///bizContent.put("timeout_express","5m");               //订单超时时间  5m代表5分钟后订单超时将不可支付，更多请查阅上面的link
        alipayTradePagePayRequest.setBizContent(bizContent.toString());
        //调用SDK将完整的表单html输出到页面
        try {
            result = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();
        } catch (AlipayApiException e) {
            // 出什么异常 我 TM 也不知道啊 直接搬砖快速接入  https://docs.open.alipay.com/270/105899/
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 回调逻辑
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public Object aliPaycallBack(HttpServletRequest request, HttpServletRequest response) throws Exception {
        logger.info("支付成功,准备解析数据返回视图");
        //将传输过来的信息存入map
        Map<String, String> params = requestToMap(request);
        //验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipayPublicKey, AlipayConfig.charset, AlipayConfig.signType);
        ModelAndView mv = new ModelAndView("pay/index");
        if (!signVerified) {
            //正确配置参数，正常情况是不可能出现检验异常
            logger.info("验证签名失败！");
            return mv;
        }
        String outTradeNo = params.get("out_trade_no");                //商户订单号
        String tradeNo = params.get("trade_no");                       //支付宝交易号
        String totalAmount = params.get("total_amount");               //付款金额
        Order order = orderService.getOrderByOutTradeNo(outTradeNo);
        Product product = productService.getProductById(order.getProductId());
        order.setTradeNo(tradeNo);
        order.setStatus(AliPayStatus.TRADE_SUCCESS.getCode());
        order.setBuyType(PayType.ALIPAY.getDesc());
        //修改订单状态为支付成功
        orderService.updateOrder(order);
        logger.info("订单号: {}", outTradeNo);
        logger.info("支付宝交易号: {}", tradeNo);
        logger.info("实付金额: {}", totalAmount);
        logger.info("购买产品: {}", product.getName());
        return mv;
    }

    private Map<String, String> requestToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            params.put(parameterName, request.getParameter(parameterName));
        }
        return params;
    }

    /**
     * 通知 理论上支付宝应该不断请求这个接口进行同步订单信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public void aliPayNotify(HttpServletRequest request, HttpServletRequest response) throws Exception {
        logger.info("支付成功, 进入通知");
        Map<String, String> params = requestToMap(request);
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipayPublicKey, AlipayConfig.charset, AlipayConfig.signType);
        if (!signVerified) {
            logger.info("验证签名失败！");
            return;
        }
        logger.info("支付宝验证签名成功！");
        String outTradeNo = params.get("out_trade_no");
        String status = params.get("trade_status");
        if (status.equals("WAIT_BUYER_PAY")) { // 等待用户付款
            logger.info("商户订单号: {} , 等待用户付款", outTradeNo);
            params.put("zh_status", AliPayStatus.WAIT_BUYER_PAY.getDesc());
        } else if (status.equals("TRADE_CLOSED")) { // 交易超时关闭以及全额退款
            logger.info("商户订单号: {} , 交易关闭", outTradeNo);
            params.put("zh_status", AliPayStatus.TRADE_CLOSED.getDesc());
        } else if (status.equals("TRADE_SUCCESS")) {  //付款成功
            logger.info("(商户订单号: {} , 付款成功/退款成功)", outTradeNo);
            params.put("zh_status", AliPayStatus.TRADE_SUCCESS.getDesc());
        } else if (status.equals("TRADE_FINISHED")) { //订单完结
            logger.info("(商户订单号: {} , 交易完结)", outTradeNo);
            params.put("zh_status", AliPayStatus.TRADE_FINISHED.getDesc());
        } else {
            logger.info("(商户订单号: {} , 迷之状态 {} ? 不可能出现的情况)", outTradeNo, status);
        }
        //管你三七二十一 我全给你存起来。
        noticeService.saveNotice(params);
    }

    /**
     * 查询订单信息
     *
     * @param outTradeNo 商户订单号
     * @param tradeNo   支付宝订单号
     * @throws AlipayApiException
     * @link https://docs.open.alipay.com/api_1/alipay.trade.query/
     */
    public Object getBillInfo(String outTradeNo, String tradeNo) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("trade_no", tradeNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        JSONObject data = new JSONObject();
        if (response.isSuccess()) {
            logger.info("查询成功！");
            String zfbId = response.getBuyerLogonId();      //支付宝账号
            String tradeStatus = response.getTradeStatus(); //订单状态
            String totalAmount = response.getTotalAmount(); //订单金额
            Date sendPayDate = response.getSendPayDate();   //订单创建时间
            //.....更多参数自己挖掘~
            data.put("zfbId", zfbId);
            data.put("tradeStatus", tradeStatus);
            data.put("totalAmount", totalAmount);
            data.put("sendPayDate", sendPayDate);
            data.put("outTradeNo", response.getOutTradeNo());
            data.put("tradeNo", response.getTradeNo());
        } else {
            logger.info("查询失败: {}", response.getSubMsg());
        }
        return ApiResponse.message(GeneralUtils.getRandomNum(13), data, ApiMessage.SUCCESS, response.getMsg());
    }


    /**
     * 交易退款
     *
     * @param outTradeNo   商户订单号
     * @param tradeNo      支付宝订单号
     * @param refundAmount 退款金额 ：圆.角分
     * @param refundReason 退款原因 ：瞎鸡儿写完事
     * @return
     * @throws AlipayApiException
     * @link https://docs.open.alipay.com/api_1/alipay.trade.refund/
     */
    public Object alipayTradeRefund(String outTradeNo, String tradeNo, double refundAmount, String refundReason) throws AlipayApiException {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("trade_no", tradeNo);
        bizContent.put("out_request_no", GeneralUtils.getRandomStr(9).toUpperCase());
        bizContent.put("refund_amount", refundAmount); //退款金额
        bizContent.put("refund_reason", refundReason); //退款原因
        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        JSONObject data = new JSONObject();
        if (response.isSuccess()) {
            data.put("gmt_refund", response.getGmtRefundPay()); //退款时间
            data.put("out_trade_no", outTradeNo);
            data.put("trade_no", tradeNo);
            data.put("refund_fee", response.getRefundFee());    //退款总金额
            logger.info("退款操作成功: {}", data);
        } else {
            logger.info("退款操作失败: {}", response.getSubMsg());
        }
        return ApiResponse.message(GeneralUtils.getRandomNum(13), data, ApiMessage.SUCCESS, response.getMsg());
    }
}
