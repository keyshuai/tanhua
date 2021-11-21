package com.tanhua.autoconfig;

import com.tanhua.autoconfig.properties.*;
import com.tanhua.autoconfig.template.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

//装配阿里云配置类
@EnableConfigurationProperties({
        AipFaceProperties.class,
        HuanXinProperties.class,
        OssProperties.class,
        SmsProperties.class,
        GreenProperties.class

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

    /**
     * 检测配置文件中 是否有tanhua.green开头的配置
     * 同时 其中enable属性=true
     * @param properties
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "tanhua.green",value = "enable", havingValue = "true")
    public AliyunGreenTemplate aliyunGreenTemplate(GreenProperties properties) {
        return new AliyunGreenTemplate(properties);
    }
}
