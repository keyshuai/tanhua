package com.tanhua.admin.task;

import com.tanhua.admin.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AnalysisTask {
    /**
     * 配置时间规则
     */
    @Autowired
    private AnalysisService analysisService;

    /**
     * 配置时间规则
     */
    @Scheduled( cron = "0/20 * * * * ? ")
    public void analysis() throws Exception {
        //业务逻辑
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("开始统计："+time);
        //调logService完成日志统计
        analysisService.analysis();
        System.out.println("结束统计");
    }
}
