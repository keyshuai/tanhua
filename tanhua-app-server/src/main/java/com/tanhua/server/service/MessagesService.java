package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class MessagesService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private UserApi userApi;

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
}
