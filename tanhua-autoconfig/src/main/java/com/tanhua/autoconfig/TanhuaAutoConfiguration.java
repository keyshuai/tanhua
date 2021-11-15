package com.tanhua.autoconfig;

import com.tanhua.autoconfig.properties.*;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

//装配阿里云配置类
@EnableConfigurationProperties({
        AipFaceProperties.class,
        GreenProperties.class,
        HuanXinProperties.class,
        OssProperties.class,
        SmsProperties.class

})

public class TanhuaAutoConfiguration {

    @Bean
    public SmsTemplate smsTemplate(SmsProperties propertie){
        return new SmsTemplate(propertie);
    }
    //阿里云存储
    @Bean
    public OssTemplate ossTemplate(OssProperties properties){return new OssTemplate(properties);}
    //百度云人脸识别
    @Bean
    public AipFaceTemplate aipFaceTemplate(AipFaceProperties properties){
        return new AipFaceTemplate();
    }
    //环信 通信
    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties properties){
        return new HuanXinTemplate(properties);
    }
}
