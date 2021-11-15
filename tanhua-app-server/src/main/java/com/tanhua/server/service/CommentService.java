package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommentService {

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String>redisTemplate;


    //分页查询评论
    public PageResult findComments(String movementId, Integer page, Integer pagesize) {
        List<Comment> list = commentApi.findComments(movementId, CommentType.COMMENT, page, pagesize);
        //判断list集合是否存在
        if (CollUtil.isEmpty(list)){
            return new PageResult();
        }
        //提取所有用户的id调用userinfoApi查询用户详情
        List<Long> userId = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userId, null);
        //构建vo对象
        ArrayList<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : list) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (userInfo!=null){
                CommentVo vo = CommentVo.init(userInfo, comment);
                commentVos.add(vo);
            }
        }
        return new PageResult(page,pagesize,0L,commentVos);
    }

    public void save(String movementId, String comment) {
        Long userId = UserHolder.getUserId();
        //添加数据
        Comment comment1 = new Comment();
        comment1.setPublishId(new ObjectId(movementId));//动态id
        comment1.setCommentType(CommentType.COMMENT.getType());//评论类型
        comment1.setContent(comment);//评论内容
        comment1.setUserId(userId);//评论用户
        comment1.setCreated(System.currentTimeMillis());//时间
        //调用Api保存评论
        Integer commentCount = commentApi.save(comment1);
        log.info("commentCount="+commentCount);

    }
    //动态点站
    public Integer likeComment(String movementId) {
        //查询是否点赞
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), CommentType.LIKE);
        if (hasComment){
            throw new BusinessException(ErrorResult.likeError());
        }
        //调用保存到Mongdb
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.save(comment);
        //拼接redis的key,将用户点赞存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey=Constants.MOVEMENT_LIKE_HASHKEY +UserHolder.getUserId();
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;
    }
    //取消点赞
    public Integer dislikeComment(String movementId) {
        //调用api查询是否点赞
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), CommentType.LIKE);
        if (!hasComment){
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //调用api 删除数据,返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        Integer count=commentApi.delete(comment);
        //拼接redis的key,将用户点赞存入redis 然后返回
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey=Constants.MOVEMENT_LIKE_HASHKEY +UserHolder.getUserId();
        redisTemplate.opsForHash().delete(key,hashKey);
        return count;

    }

    public Integer loveComment(String movementId) {
        //1、调用API查询用户是否已点赞
        Boolean hasComment = commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LOVE);
        //2、如果已经喜欢，抛出异常
        if(hasComment) {
            throw  new BusinessException(ErrorResult.loveError());
        }
        //3、调用API保存数据到Mongodb
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentApi.save(comment);
        //4、拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;
    }

    public Integer unloveComment(String movementId) {
        //1、调用API查询用户是否已点赞
        Boolean hasComment = commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LOVE);
        //2、如果未点赞，抛出异常
        if(!hasComment) {
            throw new BusinessException(ErrorResult.disloveError());
        }
        //3、调用API，删除数据，返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(UserHolder.getUserId());
        Integer count = commentApi.delete(comment);
        //4、拼接redis的key，删除点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
        redisTemplate.opsForHash().delete(key,hashKey);
        return count;
    }
    //评论点赞
    public Integer pllike(String movementId) {
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), CommentType.LIKE);
        if (hasComment){
            throw new BusinessException(ErrorResult.likeError());
        }
        //调用api
        Comment comment = new Comment();
        comment.setUserId(UserHolder.getUserId());//评论人
        comment.setPublishId(new ObjectId(movementId));//动态id
        comment.setLikeCount(CommentType.LIKE.getType());//点赞
        comment.setLikeCount(0);//当前评论点赞数
        comment.setCreated(System.currentTimeMillis());//发表时间
//        comment.setPublishUserId();
//        comment.setCommentType();

        Integer count = commentApi.hasCommentShuai(comment);
        Comment byId = commentApi.findById(new ObjectId(movementId));

        if (ObjectUtil.isEmpty(byId)){
            throw new BusinessException(ErrorResult.likeError());
        }
        //4、拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.LIKE_COUNT + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId()+"_"+movementId;
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;


    }

    public Integer displlike(String movementId) {
        //调用api 查询是否点赞
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), CommentType.LIKE);
        if (!hasComment){
            Comment comment = new Comment();
            comment.setUserId(UserHolder.getUserId());//评论人
            comment.setPublishId(new ObjectId(movementId));//动态id
            comment.setLikeCount(CommentType.LIKE.getType());//点赞
            Integer count=commentApi.displlike(comment);
            //拼接 删除redis里面的点赞状态
            String key = Constants.LIKE_COUNT + movementId;
            String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId()+"_"+movementId;
            redisTemplate.opsForHash().delete(key,hashKey);
            return count;

        }
        throw new BusinessException(ErrorResult.disLikeError());
    }
}
