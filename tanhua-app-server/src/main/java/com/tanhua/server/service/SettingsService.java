package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public void saveQuestion(String content) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        Question question = questionApi.findByUserId(userId);
        if (question==null){
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            questionApi.save(question);
        }else {
            question.setTxt(content);
            questionApi.update(question);
        }

    }

    public void saveSettings(Map map) {
        boolean likeNotification =(Boolean)map.get("likeNotification");
        boolean pinglunNotification = (boolean) map.get("pinglunNotification");
        boolean gonggaoNotification = (boolean) map.get("gonggaoNotification");

        Long userId = UserHolder.getUserId();
        Settings settings = settingsApi.findByUserId(userId);
        if (settings==null){
            settings=new Settings();
            settings.setUserId(userId);
            settings.setPinglunNotification(pinglunNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setLikeNotification(likeNotification);
            settingsApi.save(settings);
        }else {
            settings.setPinglunNotification(pinglunNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setLikeNotification(likeNotification);
            settingsApi.update(settings);
        }
    }

    public PageResult blacklist(int page, int size) {
        Long userId = UserHolder.getUserId();
        IPage<UserInfo>iPage=blackListApi.findByUserId(userId,page,size);
        //将对象转化,将查询的Ipage对象的内容封装到pageResult中
        PageResult pr=new PageResult(page,size, iPage.getTotal(), iPage.getRecords());
        return pr;
    }

    public void deleteBlackList(Long blackUserId) {
        Long userId = UserHolder.getUserId();
        blackListApi.delete(userId,blackUserId);
    }
}
