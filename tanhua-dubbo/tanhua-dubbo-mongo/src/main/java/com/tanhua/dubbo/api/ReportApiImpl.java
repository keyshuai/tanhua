package com.tanhua.dubbo.api;

import com.tanhua.model.mongos.Report;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@DubboService
public class ReportApiImpl implements ReportApi{

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Report find() {
        Query id = Query.query(Criteria.where("Id").is("1"));
        Report one = mongoTemplate.findOne(id, Report.class);
        System.out.println(one);
        return one;
    }
}
