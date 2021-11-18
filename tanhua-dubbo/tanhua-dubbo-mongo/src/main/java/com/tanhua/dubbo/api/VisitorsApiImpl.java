package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.util.List;

@DubboService
public class VisitorsApiImpl implements VisitorsApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    //保存访客数据
    @Override
    public void save(Visitors visitors) {
        Query query = Query.query(Criteria.where("userId").is(visitors.getUserId()).and("visitorUserId").is(visitors.getVisitorUserId())
                .and("visitDate").is(visitors.getVisitDate()));
        //不存在保存
        if (!mongoTemplate.exists(query,Visitors.class)){
            mongoTemplate.save(visitors);
        }
    }

    @Override
    public List<Visitors> queryMyVisitors(Long date, Long userId) {
        //查询访问首页列表
        Criteria criteria = Criteria.where("userId").is(userId);
        if (date!=null){
            criteria.and("date").gt(date);
        }
        Query query = Query.query(criteria).limit(5).with(Sort.by(Sort.Order.desc("date")));
        return mongoTemplate.find(query,Visitors.class);
    }
}
