package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    //推荐好友列表
    public PageResult recommendation(RecommendUserDto dto) {
        Long userId = UserHolder.getUserId();
        //分页查询数据列表
        PageResult pr = recommendUserApi.queryRecommendUserList(dto.getPage(), dto.getPagesize(), userId);
        List<RecommendUser> items= (List<RecommendUser>) pr.getItems();
        if (items==null||items.size()<=0){
            return pr;
        }
        //提取所有推荐的用户id列表 --工具包
        List<Long> ids = CollUtil.getFieldValues(items, "userId", Long.class);
//        List<Long> collect = items.stream().map(RecommendUser::getUserId).collect(Collectors.toList());
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(dto.getAge());
        userInfo.setGender(dto.getGender());
        //查询条件.批量查询所有用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);
        ArrayList<TodayBest> list = new ArrayList<>();
        for (RecommendUser item : items) {
            UserInfo info = map.get(item.getUserId());
            if (info!=null){
                TodayBest vo = TodayBest.init(info, item);
                list.add(vo);
            }
        }
        //构造返回值
        pr.setItems(list);
        return pr;
    }
}
