package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.util.List;

@DubboService
public class CommentApiImpl implements CommentApi {

    @Autowired //mongodb Spring控制器
    private MongoTemplate mongoTemplate;



    //发布评论,并获取评论数量
    @Override
    public Integer save(Comment comment) {
        //查询动态
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        //向comment对象设置被评论人的属性
        if (movement != null) {
            comment.setPublishUserId(movement.getUserId());
        }
        //保存到数据库
        mongoTemplate.save(comment);
        //更新动态列表中的字段
        Query query=Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType()==CommentType.LIKE.getType()){
            update.inc("likeCount",1);
        }else if (comment.getCommentType()==CommentType.COMMENT.getType()){
            update.inc("commentCount",1);
        }else {
            update.inc("loveCount",1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取最后的更新字段
        Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
        //获取最新的评论数量,并返回
        return modify.statisCount(comment.getCommentType());
    }

    @Override
    public List<Comment> findComments(String movementId, CommentType comment, Integer page, Integer pagesize) {
        Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId)).and("commentType")
                .is(comment.getType()))
                .skip((page-1)*pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        //查询并返回
        return mongoTemplate.find(query,Comment.class);
    }

    @Override
    public Boolean hasComment(String movementId, Long userId, CommentType like) {
        return null;
    }

    @Override
    public Integer delete(Comment comment) {
        return null;
    }
}
