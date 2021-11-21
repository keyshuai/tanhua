package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ManageService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private MovementApi movementApi;

    @Autowired
    private RedisTemplate redisTemplate;


    //用户列表
    public PageResult findAllUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> all = userInfoApi.findAll(page, pagesize);
        List<UserInfo> list = all.getRecords();
        for (UserInfo userInfo : list) {
            String key = Constants.USER_FREEZE + userInfo.getId();
            if (redisTemplate.hasKey(key)) {
                userInfo.setUserStatus("2");
            }
        }
        return new PageResult(page, pagesize, all.getTotal(), all.getRecords());
    }

    //根据id查询
    public UserInfo findUserById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        //查询redis中的冻结状态
        String key = Constants.USER_FREEZE + userId;
        if (redisTemplate.hasKey(key)) {
            userInfo.setUserStatus("2");
        }
        return userInfo;
    }

    //根据用户id查询此用户发布的视频
    //查询指定用户发布的所有视频列表
    public PageResult findAllVideos(Integer page, Integer pagesize, Long uid) {
        return videoApi.findByUserId(page, pagesize, uid);
    }

    //查询动态
    public PageResult findAllMovements(Integer page, Integer pagesize, Long uid, Integer state) {
        //调用API查询数据Movment对象
        PageResult result = movementApi.findByUserId(uid, state, page, pagesize);
        //解析PageResult 获取Movement对象列表
        List<Movement> list = (List<Movement>) result.getItems();
        //一个Movement对象转化为一个vo
        if (CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        List<Long> userId = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userId, null);
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : list) {
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        }
        //构造返回值
        result.setItems(vos);
        return result;
    }


    //用户冻结
    public Map userFreeze(Map params) {
        //构造key
        String userId = params.get("userId").toString();
        Integer freezingTime = Integer.valueOf(params.get("freezingTime").toString());
        int days = 0;
        if (freezingTime == 1) {
            days = 3;
        }
        if (freezingTime == 2) {
            days = 7;
        }
        if (freezingTime ==3 ){
            days = -1;
        }
        //将数据存入redis
        String value = JSON.toJSONString(params);
        redisTemplate.opsForValue().set(Constants.USER_FREEZE+userId,value,days, TimeUnit.MINUTES);
        Map map = new HashMap();
        map.put("message","冻结成功");
        return map;
    }



    //用户解冻
    public Map unfreeze(Map params) {
        Long userId =  Long.valueOf(params.get("userId").toString());
        //删除redis中的数据
        redisTemplate.delete(Constants.USER_FREEZE+userId);
        Map map =new HashMap();
        map.put("message","解冻成功");
        return map;
    }


    public VisitorsVo messagesById(Long id) {
        return null;
    }
}
