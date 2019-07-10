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
import io.springboot.pay.api.AliPayStatus;
import io.springboot.pay.api.IOrder;
import io.springboot.pay.model.Notice;
import io.springboot.pay.model.Order;
import io.springboot.pay.model.Product;
import io.springboot.pay.util.ApiMessage;
import io.springboot.pay.util.ApiResponse;
import io.springboot.pay.util.GeneralUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class OrderService implements IOrder {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductService productService;
    @Autowired
    private NoticeService noticeService;

    @Override
    public Order getOrderById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Order.class);
    }

    @Override
    public Order getOrderByOutTradeNo(String id) {
        Query query = new Query(Criteria.where("out_trade_no").is(id));
        return mongoTemplate.findOne(query, Order.class);
    }

    @Override
    public void updateOrder(Order order) {
        Query query = new Query(Criteria.where("out_trade_no").is(order.getOutTradeNo()));
        Update update = new Update();
        update.set("trade_no", order.getTradeNo());
        update.set("buy_type", order.getBuyType());
        update.set("status", order.getStatus());
        long count = mongoTemplate.updateFirst(query, update, Order.class).getModifiedCount();
        if (count == 0) {
            logger.info("没有更新数据，可能是更新失败了，或者条件没找到。可以做相应的处理");
        }
    }

    @Override
    public String getOrderList(int skip, int limit, String search) {
        Query query = new Query();
        if (search != "") {
        	if (search.contains("(") || search.contains(")")) {
				StringBuffer sb = new StringBuffer(search);
				int kh1 = 0;
				for (int i = 0; i < search.length(); i++) {
					if (search.charAt(i) == '(') {
						sb.insert(i + kh1, "\\");
						kh1++;
					}
				}
				int kh2 = 0;
				String str = sb.toString();
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == ')') {
						sb.insert(i + kh2, "\\");
						kh2++;
					}
				}
				search = sb.toString();
			}
            Criteria criteria = new Criteria();
            Pattern pattern = Pattern.compile("^.*" + search + ".*$", Pattern.CASE_INSENSITIVE); //正常应该使用精确匹配效率更高
            query.addCriteria(
                    criteria.orOperator(
                            Criteria.where("out_trade_no").regex(pattern),
                            Criteria.where("trade_no").regex(pattern)
                    )
            );
        }
        long total = mongoTemplate.count(query, Order.class);
        query.skip(skip).limit(limit);
        List<Order> orderList = mongoTemplate.find(query, Order.class);
        JSONObject table = new JSONObject();
        JSONArray rows = new JSONArray();
        JSONObject r;
        for (Order order : orderList) {
            r = new JSONObject();
            r.put("id", order.getId());
            r.put("subject", order.getSubject());
            r.put("status", order.getStatus());
            r.put("body", order.getBody());
            r.put("totalAmount", order.getTotalAmount());
            r.put("ip", order.getSpbillCreateIp());
            r.put("tradeNo", order.getTradeNo());
            String outTradeNo = order.getOutTradeNo();
            r.put("outTradeNo", order.getOutTradeNo());
            Notice notice = noticeService.getNoticeByOutTradeNo(outTradeNo);
            r.put("refundFee", notice != null ? notice.getRefundFee() : null);  //退款总金额
            rows.add(r);
        }
        table.put("total", total);
        table.put("rows", rows);
        return table.toString();
    }

    public Object saveOrder(Order order, HttpServletRequest request) {
        String productId = order.getProductId();
        Product product = productService.getProductById(productId);
        if (product == null) {
            //数据异常
            return ApiResponse.message(GeneralUtils.getRandomNum(13), ApiMessage.BAD_REQUEST);
        }
        String name = product.getName();
        String price = product.getPrice();                                  //单价
        int inventory = product.getInventory();                             //当前库存
        int count = order.getBuyCount();                                    //购买数量
        //检查库存
        if (!productService.checkInventory(count, inventory)) {
            return ApiResponse.message(GeneralUtils.getRandomNum(13), ApiMessage.PRODUCT_LACK);
        }
        //减库存 并更新
        inventory = inventory - count;
        product.setInventory(inventory);
        productService.updateInventory(product);
        String totalAmount = String.valueOf(Double.valueOf(price) * count); //算出总价格
        order.setOutTradeNo(GeneralUtils.next());
        order.setTotalAmount(totalAmount);
        order.setBuyCount(count);
        order.setSpbillCreateIp(GeneralUtils.getClientIP(request));
        order.setStatus(AliPayStatus.WAIT_BUYER_PAY.getCode());             //设置待支付状态
        order.setSubject(name);
        order.setBody("描述个球噢，买就完事了~");
        mongoTemplate.insert(order);
        return ApiResponse.success(GeneralUtils.getRandomNum(13), order);
    }
}
