package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Peachblossom;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@DubboService
public class PeachblossomApiImpl implements PeachblossomApi {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Peachblossom pea) {
        //自动增长
        pea.setVid(idWorker.getNextId("peachblossom"));
        pea.setCreated(System.currentTimeMillis());

        mongoTemplate.save(pea);
//        return pea.getIds().toHe xString();
        return null;
    }

    @Override
    public Peachblossom find(Long userId) {
       //设置排除条件 ,不能收到自己的语音
        Criteria criteria = Criteria.where("Id").nin(userId);

        //根据时间排序查询
        Query query = new Query(criteria).limit(1).with(Sort.by(Sort.Order.desc("created")));

        //修改voiceCount表中获取的次数
        Peachblossom andModify = mongoTemplate.findOne(query,Peachblossom.class);

        Query userId1 = Query.query(Criteria.where("Id").is(andModify.getId()));
        Update update=new Update();
        update.inc("remainingTimes",-1);

        mongoTemplate.findAndModify(userId1, update,  Peachblossom.class);


        log.info("" +andModify);
        return andModify;

    }


}
