package io.springboot.pay.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import io.springboot.pay.api.INotice;
import io.springboot.pay.model.Notice;

import java.util.Map;
import java.util.Set;
@Service
public class NoticeService implements INotice {

    private static final Logger logger = LoggerFactory.getLogger(NoticeService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     *  我全都要！
     *
     * @param params 支付宝通知来过的所有参数
     */
    @Override
    public void saveNotice(Map<String, String> params) {
        logger.info("Ali Notice Params: {}", params);
        Query query = new Query(Criteria.where("out_trade_no").is(params.get("out_trade_no")));
        Update update = new Update();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            update = update.set(entry.getKey(), entry.getValue());
        }
        mongoTemplate.upsert(query,update,Notice.class);
    }

    public Notice getNoticeByOutTradeNo(String outTradeNo){
        Query query = new Query(Criteria.where("out_trade_no").is(outTradeNo));
        return mongoTemplate.findOne(query, Notice.class);
    }
}
