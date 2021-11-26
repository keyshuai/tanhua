package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserLikeApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.*;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Log4j
public class UserInfoService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @DubboReference
    private UserLikeApi userLikeApi;

    //保存
    public void save(UserInfo userInfo) {
        UserInfo byId = userInfoApi.findById(userInfo.getId());
        if (byId == null) {
            userInfoApi.save(userInfo);
        }
        userInfoApi.update(userInfo);
    }

    //更新用户头像
    public void updateHead(MultipartFile headPhoto, Long id) throws IOException {
        String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

        boolean detect = aipFaceTemplate.detect(imageUrl);

        if (!detect) {
            throw new BusinessException(ErrorResult.faceError());
        } else {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(Long.valueOf(id));
            userInfo.setAvatar(imageUrl);
            userInfoApi.update(userInfo);
        }
    }

    //根据id查询
    public UserInfoVo findById(Long userID) {
        UserInfo userInfo = userInfoApi.findById(userID);

        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }

    //更新
    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }


    public CountsVo counts() {
        Long userId = UserHolder.getUserId();
        CountsVo countsVo = new CountsVo();

        //粉丝
        countsVo.setFanCount(userLikeApi.querybean(userId));
        //喜欢
        countsVo.setLoveCount(userLikeApi.querylike(userId));
        //互相喜欢
        countsVo.setEachLoveCount(userLikeApi.queryeachlove(userId));

        return countsVo;
    }

    public PageResult friends(Integer type, Integer page, Integer pagesize, String nickname) {

        Long userId = UserHolder.getUserId();
        // type： 1 互相关注 2 我关注 3 粉丝 4 谁看过我
        if (type==1){
            List<Friend> list = userLikeApi.friends(userId);
            List<Long> friendId = CollUtil.getFieldValues(list, "friendId", Long.class);
            List<UserInfo> userInfos=userInfoApi.find(friendId);
            return new PageResult(page,pagesize,0L,userInfos);
        }
        if (type==2){
            List<UserLike> list=userLikeApi.likeUserId(userId);
            log.info(list);
            List<Long> likeUserId = CollUtil.getFieldValues(list, "likeUserId", Long.class);
            log.info("输出结果"+likeUserId);
            List<UserInfo> userInfos=userInfoApi.likeUserId(likeUserId);
            return new PageResult(page,pagesize,0L,userInfos);
        }
        //粉丝列表
        if (type==3){
            //获取关注我的粉丝
            List<UserLike> list=userLikeApi.isLike(userId);
            //获取粉丝id 存入集合
            List<Long> userId1 = CollUtil.getFieldValues(list, "userId", Long.class);
            List<UserInfoLikeVo> vo = new ArrayList<>();

            Map<Long, UserInfo> map = userInfoApi.findByIds(userId1, null);

            for (UserLike userLike : list) {
                UserInfoLikeVo likeVo=new UserInfoLikeVo();
                UserInfo userInfo = map.get(userLike.getUserId());
                BeanUtils.copyProperties(userInfo,likeVo);
                UserLike userLike1 = userLikeApi.find(userInfo.getId());
                log.info("userLike 是是是是"+userLike1);

                likeVo.setAlreadyLove(userLike1.getIsLike());
                vo.add(likeVo);
                log.info(vo);
            }

            return new PageResult(page,pagesize,0L,vo);

        }
        if (type ==4 ){

        }
        return null;

    }
}
