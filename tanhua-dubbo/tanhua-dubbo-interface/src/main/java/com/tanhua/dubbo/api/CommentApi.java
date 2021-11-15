package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import org.bson.types.ObjectId;

import java.util.List;

public interface CommentApi {

    //发布评论，并获取评论数量
    Integer save(Comment comment1);

    //分页查询
    List<Comment> findComments(String movementId, CommentType comment, Integer page, Integer pagesize);

    //判断comment数据是否存在
    Boolean hasComment(String movementId, Long userId, CommentType like);

    //删除comment数据
    Integer delete(Comment comment);


    //判断评论点赞
    Integer hasCommentShuai(Comment comment);


    Comment findById(ObjectId objectId);
    //删除评论点赞
    Integer displlike(Comment comment);
}
