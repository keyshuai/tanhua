package com.tanhua.admin.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tanhua.admin.service.AnalysisService;
import com.tanhua.model.vo.AnalysisSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private AnalysisService analysisService;

    /**
     * 概要统计
     */
    @GetMapping("/summary")
    public AnalysisSummaryVo getSummary(){
        AnalysisSummaryVo analysisSummaryVo = new AnalysisSummaryVo();
//        DateTime dateTime = DateUtil.parseDate("2021-11-21");
        Date now = Calendar.getInstance().getTime();


        //累计用户数
        Integer total = analysisService.queryCumulativeUsers();
        analysisSummaryVo.setCumulativeUsers(Long.valueOf(total));
        return null;
    }

}
