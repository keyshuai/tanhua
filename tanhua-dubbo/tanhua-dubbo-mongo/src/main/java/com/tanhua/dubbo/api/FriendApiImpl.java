package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class FriendApiImpl implements FriendApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long friendId) {
        Query query = Query.query(Criteria.where("userId").is(userId).and("friendId").is(friendId));
        //判断好友关系是否存在
        if (!mongoTemplate.exists(query,Friend.class)){
            //如果不存在保存
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
        //保存好友的数据
        Query query1 = Query.query(Criteria.where("userId").is(friendId).and("friendId").is(userId));
        //判断好友关系是否存在
        if (!mongoTemplate.exists(query1,Friend.class)){
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }

    }

    @Override
    public List<Friend> findByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria).skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query,Friend.class);
    }

    @Override
    public List<Comment> like(Long userId, Integer page, Integer pagesize, CommentType like) {
        //根据id查询publ
        Criteria criteria = Criteria.where("publishUserId").is(userId).and("commentType").is(like.getType());
        Query query = Query.query(criteria).skip((page - 1) * pagesize).limit(pagesize);

        return mongoTemplate.find(query, Comment.class);
    }
}
