package io.springboot.pay.api;

import io.springboot.pay.model.Order;

public interface IOrder {
    /**
     * 获取订单信息
     *
     * @param id mongo 的 ObjectId
     * @return
     */
    Order getOrderById(String id);

    /**
     * 获取订单信息
     *
     * @param id 商家订单编号
     * @return
     */
    Order getOrderByOutTradeNo(String id);

    /**
     * 更新单条订单信息
     *
     * @param order 订单对象
     */
    void updateOrder(Order order);

    /**
     * 查询订单列表信息
     *
     * @param skip   从第M页开始显示数据
     * @param limit  显示当前页的N条数据
     * @param search 查询条件
     * @return
     */
    String getOrderList(int skip, int limit, String search);
}
