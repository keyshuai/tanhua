package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TanhuaService {
    @DubboReference
    private RecommendUserApi recommendUserApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @Autowired
    private HuanXinTemplate template;

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

    public TodayBest personalInfo(Long userId) {
        //用户id查询 用户详情
        UserInfo userInfo = userInfoApi.findById(userId);
        //根据操作人 id和查看的用户id,查询两者用户推荐
        RecommendUser user = recommendUserApi.queryByUserId(userId, UserHolder.getUserId());
        return TodayBest.init(userInfo,user);

    }

    public String strangerQuestions(Long userId) {
        Question byUserId = questionApi.findByUserId(userId);
        return byUserId == null ? "你喜欢茵蒂克斯吗" :byUserId.getTxt();
    }
    //回复陌生人信息
    public void replyQuestions(Long userId, String reply) {
        //构造消息数据
        Long currentUserId = UserHolder.getUserId();
        UserInfo userInfo = userInfoApi.findById(currentUserId);
        Map map=new HashMap<>();
        map.put("userId",currentUserId);
        map.put("huanxinId", Constants.HX_USER_PREFIX+currentUserId);
        map.put("nickname",userInfo.getNickname());
        map.put("strangerQuestion",strangerQuestions(userId));
        map.put("reply",reply);
        String message = JSON.toJSONString(map);
        //调用template对象发送消息
        Boolean aBoolean = template.sendMsg(Constants.HX_USER_PREFIX + userId, message);
        //接收
        if (!aBoolean){
            throw new BusinessException(ErrorResult.error());
        }


    }
}
