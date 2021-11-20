package com.tanhua.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;

import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate redisTemplate;


    //用户列表
    public PageResult findAllUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> all = userInfoApi.findAll(page, pagesize);
        List<UserInfo> list = all.getRecords();
        for (UserInfo userInfo : list) {
            String key=Constants.USER_FREEZE+userInfo.getId();
            if (redisTemplate.hasKey(key)){
                userInfo.setUserStatus("2");
            }
        }
        return new PageResult(page,pagesize,all.getTotal(),all.getRecords());
    }

    //根据id查询
    public UserInfo findUserById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        //查询redis中的冻结状态
        String key=Constants.USER_FREEZE+userId;
        if (redisTemplate.hasKey(key)){
            userInfo.setUserStatus("2");
        }
        return userInfo;
    }
}
