package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
@DubboService
public class RecommendUserApiImpl implements RecommendUserApi{
//    spring集成的mongodb方法
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser queryWithMaxScore(Long toUserId) {
        Criteria criteria = Criteria.where("toUserId");
        //构建Criteria
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        //调用mongoTemplate查询
        return mongoTemplate.findOne(query,RecommendUser.class);
    }

    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId) {
        return null;
    }

    @Override
    public RecommendUser queryByUserId(Long userId, Long userId1) {
        return null;
    }

    @Override
    public List<RecommendUser> queryCardsList(Long userId, int count) {
        return null;
    }
}
