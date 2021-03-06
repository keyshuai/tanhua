package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Comment;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {

    public void save(UserInfo userInfo);

    public void update(UserInfo userInfo);

    //根据id查询
    UserInfo findById(Long id);

    /**
     * 批量查询用户详情
     *    返回值：Map<id,UserInfo>
     */
    Map<Long,UserInfo> findByIds(List<Long> userIds,UserInfo info);

    //分页查询
    IPage findAll(Integer page,Integer pagesize);


    Map<Long, UserInfo> findLikes(List<Long> userIds);


    List<UserInfo> find(List<Long> friendId);

    List<UserInfo> likeUserId(List<Long> likeUserId);
}
