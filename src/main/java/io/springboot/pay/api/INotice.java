package io.springboot.pay.api;

import java.util.Map;

public interface INotice {

    /**
     * 保存通知信息[订单信息]
     *
     * @param params
     */
    void saveNotice(Map<String, String> params);
}
