package com.tanhua.server.service;

import com.tanhua.dubbo.api.QuestionsApi;
import com.tanhua.dubbo.api.ReportApi;
import com.tanhua.dubbo.api.TextSoulApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongos.Questions;
import com.tanhua.model.mongos.Report;
import com.tanhua.model.mongos.Soul;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class testSoulService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private TextSoulApi textSoulApi;

    @DubboReference
    private QuestionsApi questionsApi;

    @DubboReference
    private ReportApi reportApi;

    //问卷列表
    public List<Soul> testSoul() {
//        Long userId = UserHolder.getUserId();

        List<Soul> souls = textSoulApi.find();



        return souls;
    }


    // 提交把 map分出来， 拿到每个选项 算出总分， 匹配对应的报告 返回报告ID
    public String submit(List<HashMap> hashMaps) {
        //当前操作用户id
        Long userId = UserHolder.getUserId();

        Integer score=0;

        for (HashMap hashMap : hashMaps) {
            String questionId = (String) hashMap.get("questionId");
            if (questionId.equals("1")){
                String optionId = (String) hashMap.get("optionId");
                if (optionId.equals("1")){
                    score+=10;
                }
                if (optionId.equals("2")){
                    score+=20;
                }
                if (optionId.equals("3")){
                    score+=30;
                }
                if (optionId.equals("4")){
                    score+=40;
                }
            }
            if (questionId.equals("2")){
                String optionId = (String) hashMap.get("optionId");
                if (optionId.equals("1")){
                    score+=10;
                }
                if (optionId.equals("2")){
                    score+=20;
                }
                if (optionId.equals("3")){
                    score+=30;
                }
                if (optionId.equals("4")){
                    score+=40;
                }
            }
            if (questionId.equals("3")){
                String optionId = (String) hashMap.get("optionId");
                if (optionId.equals("1")){
                    score+=10;
                }
                if (optionId.equals("2")){
                    score+=20;
                }
                if (optionId.equals("3")){
                    score+=30;
                }
                if (optionId.equals("4")){
                    score+=40;
                }
            }
        }

        return score.toString();
    }


    public Report report(String id) {
        Report report=reportApi.find();


        return report;
    }
}
