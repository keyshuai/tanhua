package com.tanhua.server.service;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class TanhuaService {
    @DubboReference
    private RecommendUserApi recommendUserApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    //查询今日佳人的数据
    public TodayBest todayBest() {
        //
        Long userId = UserHolder.getUserId();
        //2、调用API查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null){
             recommendUser = new RecommendUser();
             recommendUser.setUserId(1l);
             recommendUser.setScore(99d);
        }
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        return vo;
    }
}
