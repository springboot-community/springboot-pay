package io.springboot.pay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import io.springboot.pay.api.IProduct;
import io.springboot.pay.model.Product;

import java.util.List;

@Service
public class ProductService implements IProduct {
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Override
    public Product getProductById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Product.class);
    }

    /**
     * 这里偷懒了因为就初始化3个商品
     * 请参考 OrderService.getOrderList
     * @param skip   从第M页开始显示数据
     * @param limit  显示当前页的N条数据
     * @param search 查询条件
     * @return
     */
    @Override
    public String getProductList(int skip, int limit, String search) {
        int total = 3;
        Query query = new Query();
        List<Product> productList = mongoTemplate.find(query, Product.class);
        JSONObject table = new JSONObject();
        JSONObject r;
        JSONArray rows = new JSONArray();
        for (Product product : productList) {
            r = new JSONObject();
            r.put("uid", product.getId());
            r.put("name", product.getName());
            r.put("price", product.getPrice());
            r.put("inventory", product.getInventory());
            rows.add(r);
        }
        table.put("total", total);
        table.put("rows", rows);
        return table.toString();
    }

    @Override
    public void updateInventory(Product product) {
        String uid = product.getId();
        Query query = new Query(Criteria.where("_id").is(uid));
        Update update = new Update();
        update.set("inventory", product.getInventory());
        long count = mongoTemplate.updateFirst(query, update, Product.class).getModifiedCount();
        if (count == 0) {
            logger.info("更新库存失败了。可以做相应的处理");
        }
    }

    /**
     * 粗略的检查库存？
     *
     * @param count     购买数
     * @param inventory 库存数
     * @return
     */
    public boolean checkInventory(int count, int inventory) {
        if (count > inventory) {
            logger.info("购买数量大于库存，下单失败~");
            return false;
        }
        return true;
    }

}
