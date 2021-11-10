package com.tanhua.server.service;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.BindException;

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
        UserInfo byId = userInfoApi.findById(userInfo.getId());
        if (byId==null){
            userInfoApi.save(userInfo);
        }
          userInfoApi.update(userInfo);
    }

    //更新用户头像
    public void updateHead(MultipartFile headPhoto,Long id) throws IOException {
        String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

        boolean detect = aipFaceTemplate.detect(imageUrl);

        if (!detect){
            throw new BusinessException(ErrorResult.faceError());
        }else {
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
        BeanUtils.copyProperties(userInfo,vo);
        if (userInfo.getAge()!=null){
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }
    //更新
    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }
}
