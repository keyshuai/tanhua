package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
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
        //查询今日佳人 where 查询此id
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //构建Criteria
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        //调用mongoTemplate查询
        return mongoTemplate.findOne(query,RecommendUser.class);
    }

    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId) {
        //构建Criteria对象
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(pagesize)
                .skip((page-1)*pagesize);
        //调用mongoTemplate查询
        List<RecommendUser> list = mongoTemplate.find(query, RecommendUser.class);
        long count = mongoTemplate.count(query, RecommendUser.class);
        return new PageResult(page,pagesize,count,list);
    }

    @Override
    public RecommendUser queryByUserId(Long userId, Long userId1) {
        Criteria criteria = Criteria.where("toUserId").is(userId1).and("userId").is(userId);
        Query query=new Query(criteria);
        RecommendUser user = mongoTemplate.findOne(query, RecommendUser.class);
        if (user==null){
            user=new RecommendUser();
            user.setUserId(userId);
            user.setToUserId(userId1);
            //构建虚假缘分值
            user.setScore(95d);

        }
        return user;
    }

    @Override
    public List<RecommendUser> queryCardsList(Long userId, int count) {
        //查询不喜欢用户id
        List<UserLike> likeList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
        List<Long> likeUserId = CollUtil.getFieldValues(likeList, "likeUserId", Long.class);
//        构造查询条件
        Criteria criteria = Criteria.where("toUserId").is(userId).and("userId").nin(likeUserId);
        //使用统计函数,随机获取推荐列表
        TypedAggregation<RecommendUser> newAggregation = TypedAggregation.newAggregation(RecommendUser.class,
                Aggregation.match(criteria),
                Aggregation.sample(count));

        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(newAggregation, RecommendUser.class);
        return results.getMappedResults();
    }
}
