package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
}
