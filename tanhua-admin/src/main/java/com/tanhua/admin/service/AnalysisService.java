package com.tanhua.admin.service;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.model.domain.Analysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AnalysisService {

    /**
     * 查询活跃用户的数量
     */




    @Autowired
    private AnalysisMapper analysisMapper;

    @Autowired
    private LogMapper logMapper;

    /**
     * 定时统计日志数据到统计表中
     * 1、查询tb_log表中的数 （每日注册用户数，每日登陆用户，活跃的用户数据，次日留存的用户）
     * 2、构造AnalysisByDay对象
     * 3、完成统计数据的更新或者保存
     */
    public void analysis() throws Exception {
        Date today = new Date();
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(today);
        //工具类 - 昨天
        String yestodayStr = DateUtil.yesterday().toString("yyyy-MM-dd");

        //1、统计每日注册用户数
        Integer numRegistered = logMapper.queryByTypeAndLogTime("0102", todayStr);
        //2、统计每日登陆用户
        Integer numLogin = logMapper.queryByTypeAndLogTime("0101", todayStr);
        //3、统计活跃的用户数
        Integer numActive = logMapper.queryByLogTime(todayStr);
        //4、统计次日留存的用户数
        Integer numRetention1d = logMapper.queryNumRetention1d(todayStr, yestodayStr);
        //5、根据当前时间查询AnalysisByDay数据
        LambdaQueryWrapper<Analysis> qw = Wrappers.<Analysis>lambdaQuery();


        qw.eq(Analysis::getRecordDate, today);

        Analysis analysis = analysisMapper.selectOne(qw);
        if (analysis == null) {
            //7、如果不存在，保存
            analysis = new Analysis();
            analysis.setRecordDate(today);
            analysis.setNumRegistered(numRegistered);
            analysis.setNumLogin(numLogin);
            analysis.setNumActive(numActive);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setCreated(new Date());
            analysis.setUpdated(new Date());
            analysisMapper.insert(analysis);
        } else {
            //8、如果存在，更新
            analysis.setNumRegistered(numRegistered);
            analysis.setNumLogin(numLogin);
            analysis.setNumActive(numActive);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setUpdated(new Date());
            analysisMapper.updateById(analysis);
        }
    }


    public Integer queryCumulativeUsers() {
        return analysisMapper.queryCumulativeUsers();
    }
}