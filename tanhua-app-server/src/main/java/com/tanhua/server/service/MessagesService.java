package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.properties.HuanXinProperties;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.vo.*;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
@Service
public class MessagesService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private UserApi userApi;

    @DubboReference
    private FriendApi friendApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;
    //用户
    public UserInfoVo findUserInfoHuanxin(String huanxinId) {
        User user = userApi.findByHuanxin(huanxinId);
        UserInfo userinfo = userInfoApi.findById(user.getId());

        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userinfo,vo);
        if (userinfo.getAge()!=null){
            vo.setAge(userinfo.getAge().toString());
        }
        return vo;
    }

    public void contacts(Long userId) {
        //1、将好友关系注册到环信
        Boolean aBoolean = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + UserHolder.getUserId(),
                Constants.HX_USER_PREFIX + userId);
        if (!aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
        //注册成功记录好友关系到mongodb
        friendApi.save(UserHolder.getUserId(),userId);
    }

    //分页查询联系人方法
    public PageResult findFriends(Integer page, Integer pagesize, String keyword) {
    //调用api查询当前用户的好友数据
        List<Friend> list = friendApi.findByUserId(UserHolder.getUserId(), page, pagesize);
        if (CollUtil.isEmpty(list)){
            return new PageResult();
        }
        //2.提取数据列表好友
        List<Long> friendId = CollUtil.getFieldValues(list, "friendId", Long.class);
        //3.调用UserInfoApi
        UserInfo info = new UserInfo();
        info.setNickname(keyword);
        Map<Long, UserInfo> map = userInfoApi.findByIds(friendId, info);
        //构造vo对象
        List<ContactVo> vos = new ArrayList<>();
        for (Friend friend : list) {
            UserInfo userInfo = map.get(friend.getFriendId());
            if(userInfo != null) {
                ContactVo vo = ContactVo.init(userInfo);
                vos.add(vo);
            }
        }
        return new PageResult(page,pagesize,0L,vos);
    }

    //评论点赞查询
    public PageResult like(Integer page, Integer pagesize) {
        //用户id
        Long user = UserHolder.getUserId();

        List <Comment> list=friendApi.like(user,page,pagesize, CommentType.LIKE);
        return getPageResult(page, pagesize, list);
    }
    //喜欢查询
    public PageResult love(Integer page, Integer pagesize) {
        Long user = UserHolder.getUserId();

        List <Comment> list=friendApi.like(user,page,pagesize, CommentType.LOVE);
        return getPageResult(page, pagesize, list);
    }
    //评论查询
    public PageResult comments(Integer page, Integer pagesize) {
        Long user = UserHolder.getUserId();
        //查询动态
        List <Comment> list=friendApi.like(user,page,pagesize, CommentType.COMMENT);

        return getPageResult(page, pagesize, list);
    }



    private PageResult getPageResult(Integer page, Integer pagesize, List<Comment> list) {

        if (CollUtil.isEmpty(list)){
            return new PageResult();
        }
        //提取数据列表
        List<Long> userId = CollUtil.getFieldValues(list, "userId", Long.class);

        log.info(userId);
        //根据id获取用户详情
        Map<Long, UserInfo> likes = userInfoApi.findLikes(userId);
        //构建vo对象

        List<CommentVo> vos=new ArrayList<>();
        for (Comment comment : list) {
            UserInfo userInfo = likes.get(comment.getPublishUserId());
            if (userInfo!=null){
                CommentVo vo = CommentVo.init(userInfo, comment);
                vos.add(vo);
            }
        }
        return new PageResult(page, pagesize,0L,vos);
    }
}
