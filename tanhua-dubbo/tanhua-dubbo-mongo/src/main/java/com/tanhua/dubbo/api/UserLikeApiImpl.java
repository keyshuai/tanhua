package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.CountsVo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;


@DubboService
public class UserLikeApiImpl implements UserLikeApi{

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike) {
        //查询数据
        try {
            Query query=Query.query(Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            if (userLike ==null){
                userLike=new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                userLike.setIsLike(isLike);
                mongoTemplate.save(userLike);
            }else {
                //更新
                Update update = Update.update("isLike", isLike);
                update.set("updated",System.currentTimeMillis());
                mongoTemplate.updateFirst(query,update,UserLike.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    //喜欢
    public Integer querylike(Long userId) {
        return (int) mongoTemplate.count(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
    }

    @Override
    public Integer querybean(Long userId) {
        return (int) mongoTemplate.count(Query.query(Criteria.where("likeUserId").is(userId)), UserLike.class);
    }

    @Override
    public Integer queryeachlove(Long userId) {
        // 思路：首先查询我的喜欢列表，然后，在我的喜欢的人范围内，查询喜欢我的人
        Query query=Query.query(Criteria.where("userId").is(userId));
        List<Friend> list = mongoTemplate.find(query, Friend.class);
        // 收集到我的喜欢列表中的用户id（对方）
        List<Long> likeUserIds = new ArrayList<>();
        for (Friend friend : list) {
            likeUserIds.add(friend.getFriendId());
        }

        // 在我的喜欢列表范围内，查询喜欢我的人有哪些
        Query query1 = Query.query(Criteria.where("userId").in(likeUserIds).and("friendId").is(userId));

        return (int)mongoTemplate.count(query1,Friend.class);
    }

    @Override
    public List<Friend> friends(Long userId) {
        // 思路：首先查询我的喜欢列表，然后，在我的喜欢的人范围内，查询喜欢我的人
        Query query=Query.query(Criteria.where("userId").is(userId));
        // 收集到我的喜欢列表中的用户id（对方）

        return mongoTemplate.find(query, Friend.class);
    }

    @Override
    public List<UserLike> likeUserId(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query,UserLike.class);
    }

    @Override
    public List<UserLike> isLike(Long userId) {
        Query query = Query.query(Criteria.where("likeUserId").is(userId));

        return mongoTemplate.find(query,UserLike.class);
    }

    @Override
    public UserLike find(Long aLong) {
        Query query = Query.query(Criteria.where("userId").is(aLong));
        return mongoTemplate.findOne(query, UserLike.class);
    }

}
