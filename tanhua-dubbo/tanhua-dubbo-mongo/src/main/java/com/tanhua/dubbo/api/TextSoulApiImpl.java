package com.tanhua.dubbo.api;

import com.tanhua.model.mongos.Questions;
import com.tanhua.model.mongos.Soul;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class TextSoulApiImpl implements TextSoulApi{

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public List<Soul> find() {


        Query id = Query.query(Criteria.where("id").is("1"));
        Soul one = mongoTemplate.findOne(id, Soul.class);
        List<Questions> questions = mongoTemplate.findAll(Questions.class);
        one.setQuestions(questions);


        Query ids = Query.query(Criteria.where("id").is("2"));
        Soul ones = mongoTemplate.findOne(ids, Soul.class);
        List<Questions> questionss = mongoTemplate.findAll(Questions.class);
        ones.setQuestions(questionss);


        Query idss = Query.query(Criteria.where("id").is("3"));
        Soul oness = mongoTemplate.findOne(idss, Soul.class);
        List<Questions> questionsss = mongoTemplate.findAll(Questions.class);
        oness.setQuestions(questionsss);

        List<Soul> souls = new ArrayList<>();
        souls.add(one);
        souls.add(ones);
        souls.add(oness);

        return souls;
    }
}
