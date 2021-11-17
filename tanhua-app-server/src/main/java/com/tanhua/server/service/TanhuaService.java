package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.NearUserVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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


    @DubboReference
    private UserLocationApi userLocationApi;

    @Autowired
    private HuanXinTemplate template;

    @DubboReference
    private UserLikeApi userLikeApi;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessagesService messagesService;

//    @Value("${tanhua.default.recommend.class}")
    private String recommendUser;

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

    public List<TodayBest> cards() {
        //调查推荐api查询数据列表
        List<RecommendUser> users = recommendUserApi.queryCardsList(UserHolder.getUserId(), 10);
        //判断是否存在,如果不存在,构造默认1,2,3
        if (CollUtil.isEmpty(users)){
            users =new ArrayList<>();
            String[] userId = recommendUser.split(",");
            for (String s : userId) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(s));
                recommendUser.setToUserId(UserHolder.getUserId());
                recommendUser.setScore(RandomUtil.randomDouble(60,90));
                users.add(recommendUser);
            }
        }
        //构造vo
        List<Long> id = CollUtil.getFieldValues(users, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(id, null);

        List<TodayBest> vos = new ArrayList<>();
        for (RecommendUser user : users) {
            UserInfo userInfo = map.get(user.getUserId());
            if (userInfo!=null){
                TodayBest vo = TodayBest.init(userInfo, user);
                vos.add(vo);
            }
        }

        return vos;
    }

    public void likeUser(Long likeUserId) {
        //调用api 保存喜欢数据(保存到Mongodb服务器中)
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(), likeUserId, true);
        if (!save){
            throw new BusinessException(ErrorResult.error());
        }
        redisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());
        redisTemplate.opsForSet().add(Constants.USER_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());
        //判断是否双向喜欢
        if (isLike(likeUserId,UserHolder.getUserId())){
            //添加好友
            messagesService.contacts(likeUserId);
        }
    }

    public Boolean isLike(Long userId,Long likeUserId){
        String key = Constants.USER_LIKE_KEY + userId;
        return redisTemplate.opsForSet().isMember(key,likeUserId.toString());
    }

    public void notLikeUser(Long likeUserId) {
        //调用api保存喜欢的数据
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(), likeUserId, false);
        if (!save){
            //失败
            throw new BusinessException(ErrorResult.noUser());
        }
        //操作redis 写入喜欢的数据,删除喜欢的数据
        redisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());
        redisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());
        //删除好友


    }

    public List<NearUserVo> queryNearUser(String gender, String distance) {
        List<Long> userIds = userLocationApi.queryNearUser(UserHolder.getUserId(), Double.valueOf(distance));
        //判断集合是否为空
        if (CollUtil.isEmpty(userIds)){
            return new ArrayList<>();
        }
        //调用userinfo根据用户id查询用户详情
        UserInfo userInfo = new UserInfo();
        userInfo.setGender(gender);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
        List<NearUserVo> vos = new ArrayList<>();
        for (Long userId : userIds) {
            //排除当前用户id
            if (userId==UserHolder.getUserId()){
                continue;
            }
            UserInfo info = map.get(userId);
            if (info!=null){
                NearUserVo v = NearUserVo.init(info);
                vos.add(v);
            }
        }
        return vos;

    }

}
