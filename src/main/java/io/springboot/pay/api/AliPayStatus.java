package io.springboot.pay.api;

public enum AliPayStatus {

    WAIT_BUYER_PAY(1, "等待付款"),
    TRADE_CLOSED(2, "订单关闭"),
    TRADE_SUCCESS(3, "支付成功"),
    TRADE_FINISHED(4, "交易完结")
    ;
    private int code;
    private String desc;

    AliPayStatus(String desc) {
        this.desc = desc;
    }

    AliPayStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
