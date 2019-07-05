package io.springboot.pay.api;

public enum PayType {

    ALIPAY(1, "支付宝"),
    WECHATPAY(2, "微信支付")
    ;
    private int code;
    private String desc;

    PayType(String desc) {
        this.desc = desc;
    }

    PayType(int code, String desc) {
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
