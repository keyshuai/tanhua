package com.tanhua.server.service;

import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private SettingsApi settingsApi;

    @DubboReference
    private BlackListApi blackListApi;

    //查询通用设置
    public SettingsVo settings(){
        SettingsVo vo = new SettingsVo();
        Long userId = UserHolder.getUserId();
        vo.setId(userId);
        vo.setPhone(UserHolder.getMobile());
        //获取陌生人的问题
        Question question = questionApi.findByUserId(userId);
        String txt= question == null ? "你喜欢java吗" :question.getTxt();
        vo.setStrangerQuestion(txt);
        //获取用户app通知开关数据
        Settings settings = settingsApi.findByUserId(userId);
        if (settings!=null){
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
            vo.setLikeNotification(settings.getLikeNotification());
        }
        return vo;
    }
}
