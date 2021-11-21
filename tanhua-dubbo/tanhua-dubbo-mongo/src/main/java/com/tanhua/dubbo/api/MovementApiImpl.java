package com.tanhua.dubbo.api;


import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TimeLineService timeLineService;

    //发布动态
    @Override
    public String publish(Movement movement) {

        try {
            //保存动态数据
            movement.setPid(idWorker.getNextId("movement"));
            movement.setCreated(System.currentTimeMillis());
            mongoTemplate.save(movement);
            //保存好友时间线数据
            timeLineService.saveTimeLine(movement.getUserId(),movement.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movement.getId().toHexString();
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId).and("state").is(1);
        Query query = Query.query(criteria).skip((page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return new PageResult(page,pagesize,0l,movements);
    }
    //动态查询
    @Override
    public List<Movement> findFriendMovements(Integer page, Integer pagesize, Long userId) {
        Query query = Query.query(Criteria.where("friendId").in(userId))
                .skip((page - 1)*pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<MovementTimeLine> lines = mongoTemplate.find(query, MovementTimeLine.class);
        //提取动态id集合
        List<ObjectId> movementId = CollUtil.getFieldValues(lines, "movementId", ObjectId.class);
        //根据动态id查询动态详情
        Query movementQuery = Query.query(Criteria.where("id").in(movementId).and("state").is(1));
        return mongoTemplate.find(movementQuery,Movement.class);
    }

    //根据pid查询
    @Override
    public List<Movement> findMovementsByPids(List<Long> pids) {
        Query query = Query.query(Criteria.where("pid").in(pids));
        return mongoTemplate.find(query, Movement.class);
    }

    //随机查询多条数据
    @Override
    public List<Movement> randomMovements(Integer counts) {
        TypedAggregation aggregation = Aggregation.newAggregation(Movement.class, Aggregation.sample(counts));
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation,Movement.class);
        return results.getMappedResults();
    }

    @Override
    public Movement findById(String movementId) {
        return mongoTemplate.findById(movementId,Movement.class);
    }

    @Override
    public PageResult findByUserId(Long uid, Integer state, Integer page, Integer pagesize) {
        Query query = new Query();
        if (uid !=null){
            query.addCriteria(Criteria.where("userId").is(uid));
        }
        if (state!=null){
            query.addCriteria(Criteria.where("state").is(state));
        }
        long count = mongoTemplate.count(query, Movement.class);
        query.limit(pagesize).skip((page-1)*pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Movement> list = mongoTemplate.find(query, Movement.class);
        return new PageResult(page,pagesize,count,list);

    }

    @Override
    public void update(String movementId, int state) {
        Query query = Query.query(Criteria.where("id").is(new ObjectId(movementId)));
        Update update=Update.update("state",state);
        mongoTemplate.updateFirst(query,update,Movement.class);
    }


}
