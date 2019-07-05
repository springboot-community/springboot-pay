package io.springboot.pay.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "pay_notice")
public class Notice {
    @Id
    private String id;
    @Field(value = "out_trade_no")
    private String outTradeNo;          //商户订单号
    @Field(value = "trade_no")
    private String tradeNo;             //支付宝交易号
    @Field(value = "trade_status")
    private String tradeStatus;         //订单状态
    @Field(value = "zh_status")
    private String zhStatus;            //订单状态中文
    @Field(value = "buyer_id")
    private String buyerId;             //买家id
    @Field(value = "receipt_amount")
    private String receiptAmount;       //实际收款金额
    @Field(value = "buyer_pay_amount")
    private String buyerPayAmount;      //买家实际付款金额
    @Field(value = "gmt_payment")
    private String gmtPayment;            //支付时间

    @Field(value = "gmt_refund")
    private String gmtRefund;             //第一次退款时间
    @Field(value = "refund_fee")
    private String refundFee;             //退款总金额

}
