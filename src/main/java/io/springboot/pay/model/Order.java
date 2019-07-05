package io.springboot.pay.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "pay_order")
public class Order {
	@Id
	private String id;
	@Field(value = "product_id")
	private String productId;// 商品ID
	@Field(value = "subject")
	private String subject;//订单名称 
	@Field(value = "body")
	private String body;// 商品描述
	@Field(value = "total_amount")
	private String totalAmount;// 订单总金额，单位为元，精确到小数点后两位
	@Field(value = "out_trade_no")
	private String outTradeNo;// 订单号(唯一)
	@Field(value = "trade_no")
	private String tradeNo;   //支付宝交易号
	@Field(value = "spbill_create_ip")
	private String spbillCreateIp;// 订单创建IP地址
	@Field(value = "buy_count")
	private int buyCount; //购买数量
	@Field(value = "buy_type")
	private String buyType; //购买方式  暂只支持支付宝
	private int status; // 订单状态

}
