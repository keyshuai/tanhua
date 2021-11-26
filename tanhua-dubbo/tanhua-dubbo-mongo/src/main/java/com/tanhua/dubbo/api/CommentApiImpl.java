package com.tanhua.dubbo.api;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Video;
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
        //更新动态列表中的字段 --------------和取消一样
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", 1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", 1);
        } else {
            update.inc("loveCount", 1);
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
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(movementId)).and("commentType")
                        .is(comment.getType()))
                .skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        //查询并返回
        return mongoTemplate.find(query, Comment.class);
    }

    //判断comment是否存在
    @Override
    public Boolean hasComment(String movementId, Long userId, CommentType commentType) {
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("publishId").is(new ObjectId(movementId))
                .and("commentType").is(commentType.getType());
        Query query = Query.query(criteria);
        return mongoTemplate.exists(query, Comment.class);
    }

    @Override
    public Integer delete(Comment comment) {
        //删除点赞
        Criteria criteria = Criteria.where("userId").is(comment.getUserId())
                .and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType());
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Comment.class);
        //更新动态列表中的字段 --------------和新增一样
        Query movementQuery = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", -1);
        } else {
            update.inc("loveCount", -1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取最后的更新字段
        Movement modify = mongoTemplate.findAndModify(movementQuery, update, options, Movement.class);
        //获取最新的评论数量,并返回
        return modify.statisCount(comment.getCommentType());

    }

    @Override
    public Integer hasCommentShuai(Comment comment) {
        Comment c = mongoTemplate.findById(comment.getPublishId(), Comment.class);

        if (ObjectUtil.isAllEmpty(c)) {
            throw new RuntimeException("评论的数据不存在");
        }

        comment.setPublishUserId(c.getPublishUserId());
        mongoTemplate.save(comment);
        //更新字段
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        update.inc("likeCount", 1);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Comment modify = mongoTemplate.findAndModify(query, update, options, Comment.class);

        return modify.getLikeCount();
    }

    @Override
    public Comment findById(ObjectId objectId) {
        return mongoTemplate.findById(objectId, Comment.class);
    }

    @Override
    public Integer displlike(Comment comment) {
        Criteria criteria = Criteria.where("userId").is(comment.getUserId())
                .and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType());
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Comment.class);
        //删除字段中的列表
        Query id = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        update.inc("likeCount", -1);
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Comment andModify = mongoTemplate.findAndModify(id, update, options, Comment.class);
        return andModify.getLikeCount();
    }

    @Override
    public Integer saves(Comment comment) {
        //查询动态
        Video movement = mongoTemplate.findById(comment.getPublishId(), Video.class);
        if (movement != null) {
            comment.setPublishUserId(movement.getUserId());
        }
        //保存到数据
        mongoTemplate.save(comment);
        //更新动态字段
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.ATTENTION.getType()) {
            update.inc("likeCount", 1);
        } else if (comment.getCommentType() == CommentType.LIKES.getType()) {
            update.inc("loveCount", 1);
        } else {
            update.inc("commentCount", 1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Video modify = mongoTemplate.findAndModify(query, update, options, Video.class);
        //获取最新的评论数量,并返回
        return modify.statisCount(comment.getCommentType());
    }

    @Override
    public Integer videoSave(Comment comment) {
        Video id = mongoTemplate.findById(comment.getPublishId(), Video.class);
        if (id != null) {
            comment.setPublishUserId(comment.getUserId());
        }
        mongoTemplate.save(comment);
        Query query = Query.query(Criteria.where("Id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", 1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount",1);
        }else {
            update.inc("loveCount",1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Video modify = mongoTemplate.findAndModify(query, update, options, Video.class);
        return modify.statisCount(comment.getCommentType());
    }

    @Override
    public Integer deleteVideo(Comment comment) {
        Criteria criteria = Criteria.where("userId").is(comment.getUserId())
                .and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType());
        mongoTemplate.remove(Query.query(criteria),Comment.class);
        //减1
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update=new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount",-1);
        }else {
            update.inc("loveCount",-1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Video modify = mongoTemplate.findAndModify(query, update, options, Video.class);
        return modify.statisCount(comment.getCommentType());

    }


}
