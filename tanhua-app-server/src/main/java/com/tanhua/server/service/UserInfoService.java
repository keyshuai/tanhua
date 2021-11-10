package com.tanhua.server.service;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    //保存
    public void save(UserInfo userInfo){
        userInfoApi.save(userInfo);
    }

    //更新用户头像
    public void updateHead(MultipartFile headPhoto,Long id) throws IOException {
        String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

        boolean detect = aipFaceTemplate.detect(imageUrl);

        if (!detect){
            throw new RuntimeException("");
        }else {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(Long.valueOf(id));
            userInfo.setAvatar(imageUrl);
            userInfoApi.update(userInfo);
        }
    }


    public UserInfoVo findById(Long userID) {
        UserInfo userInfo = userInfoApi.findById(userID);

        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,vo);
        if (userInfo.getAge()!=null){
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }
}
