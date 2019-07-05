package io.springboot.pay.api;

import io.springboot.pay.model.Product;

public interface IProduct {
    /**
     * 获取商品信息
     *
     * @param id mongo的ObjectId
     * @return
     */
    Product getProductById(String id);

    /**
     * 获取商品列表
     *
     * @param skip   从第M页开始显示数据
     * @param limit  显示当前页的N条数据
     * @param search 查询条件
     * @return
     */
    String getProductList(int skip, int limit, String search);

    /**
     * 更新商品库存
     *
     * @param product 商品对象
     */
    void updateInventory(Product product);
}
