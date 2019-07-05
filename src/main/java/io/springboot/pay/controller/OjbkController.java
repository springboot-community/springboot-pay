package io.springboot.pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import io.springboot.pay.model.Order;
import io.springboot.pay.model.Product;
import io.springboot.pay.service.OrderService;
import io.springboot.pay.service.ProductService;
import io.springboot.pay.util.ApiResponse;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pay")
public class OjbkController {
    @Autowired
    private ProductService productService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private OrderService orderService;

    /**
     * 产品信息主页
     *
     * @return
     */
    @RequestMapping("productindex.php")
    public ModelAndView goProductIndex() {
        ModelAndView mv = new ModelAndView("/pay/product");
        return mv;
    }

    /**
     * 订单信息主页
     *
     * @return
     */
    @RequestMapping("orderindex.php")
    public ModelAndView goOrderIndex() {
        ModelAndView mv = new ModelAndView("/pay/order");
        return mv;
    }

    @RequestMapping("success.php")
    public Object success(){
        ModelAndView mv = new ModelAndView("/pay/index");
        return mv;
    }
    /**
     * 初始化补充库存
     * 瞎鸡儿初始化数据
     *
     * @return
     */
    @RequestMapping("init.php")
    public Object initialize() {
        String data[][] = {{"iPhone Xs Max 256G", "233.33"}, {"Macbook Pro 15.4", "499.99"}, {"iPad Pro 256G", "100"}};
        /*mongoTemplate.dropCollection(Product.class);
        List<Product> productList = new ArrayList<>();
        for(int i =0;i<data.length;i++ ){
            Product product = new Product();
            product.setName(data[i][0]);
            product.setInventory(9999);
            product.setPrice(data[i][1]);
            productList.add(product);
        }
        mongoTemplate.insertAll(productList);*/
        for (int i = 0; i < data.length; i++) {
            Query query = new Query(Criteria.where("name").is(data[i][0]));
            Update update = new Update();
            update.set("name", data[i][0]);
            update.set("inventory", 9999);
            update.set("price", data[i][1]);
            mongoTemplate.upsert(query, update, Product.class);
        }
        return ApiResponse.success();
    }

    /**
     * 获取产品列表
     * 因为产品初始化就设置了三个
     * 因此就不搞什么分页参数了
     *
     * @return
     */
    @RequestMapping("getproduct.php")
    public Object getProductList(int skip, int limit, String search) {
        return productService.getProductList(skip, limit, search);
    }

    /**
     * 保存订单
     *
     * @param order
     * @param request
     * @return
     */
    @RequestMapping("saveorder.php")
    public Object saveOrder(Order order, HttpServletRequest request) {
       return orderService.saveOrder(order, request);
    }

    /**
     * 获取订单
     *
     * @return
     */
    @RequestMapping("getorder.php")
    public Object getOrderList(int skip, int limit, String search) {
        return orderService.getOrderList(skip, limit, search);
    }

}
