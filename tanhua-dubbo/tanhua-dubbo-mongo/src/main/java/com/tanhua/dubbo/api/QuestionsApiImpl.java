package com.tanhua.dubbo.api;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@DubboService
public class QuestionsApiImpl implements QuestionsApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String findOne() {
        Query.query(Criteria.where("id"));
        return null;
    }
}
