package io.springboot.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {
	@Value("${ali.pay.app.id}")
	private String appId;
	@Value("${ali.pay.merchant.private.key}")
	private String merchantPrivateKey;
	public static String alipayPublicKey;
	@Value("${ali.pay.zbf.public.key}")
	public void setAlipayPublicKey(String alipayPublicKey){
		AlipayConfig.alipayPublicKey = alipayPublicKey;
	}
	@Value("${ali.pay.gateway.url:https://openapi.alipaydev.com/gateway.do}")
	private String gatewayUrl; //正式环境换为 https://openapi.alipay.com/gateway.do
	private static String format = "json";
	public static String signType = "RSA2";
	public static String charset = "utf-8";

	@Bean("alipayClient")
	public AlipayClient setAlipayClient(){
		AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, appId, merchantPrivateKey, format, charset, alipayPublicKey, signType);
		return alipayClient;
	}


}
