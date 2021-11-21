package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private SmsTemplate template;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UserFreezeService userFreezeService;

    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserApi userApi;

    @Autowired
    private AmqpTemplate amqpTemplate;


    //发送验证码
    public void sendMsg(String phone) {
        //校验用户状态
        User user =userApi.findByMobile(phone);
        if (user!=null){
            userFreezeService.checkUserStatus(1,user.getId());
        }


        //1、随机生成6位数字
        //String code = RandomStringUtils.randomNumeric(6);
        String code = "123456";
        //2、调用template对象，发送手机短信
        //template.sendSms(phone,code);
        //判断验证码是否还未失效
        if (redisTemplate.hasKey("CHECK_CODE_" + phone)) {
            throw new RuntimeException("验证码还未失效");
        }
        //3、将验证码存入到redis
        redisTemplate.opsForValue().set("CHECK_CODE_"+phone,code, Duration.ofMinutes(5));
    }

    //验证登录
    public Map loginVerification(String phone, String code) {
        String redisCode = redisTemplate.opsForValue().get("CHECK_CODE_" + phone);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)){
            //验证码无效
            throw new BusinessException(ErrorResult.loginError());
        }
        redisTemplate.delete("CHECK_CODE_"+phone);
        User user = userApi.findByMobile(phone);
        boolean isNew = false;
        String type="0101";//登录
        if (user ==null){
            type ="0102";//注册
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long userId=userApi.save(user);
            user.setId(userId);
            isNew=true;

            //注册环形用户
            String hxUser ="hx"+user.getId();
            Boolean create = huanXinTemplate.createUser(hxUser, Constants.INIT_PASSWORD);
            if (create){
                user.setHxUser(hxUser);
                user.setPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }
        }

        mqMessageService.sendLogMessage(user.getId(),type,"user",null);
        //通过JWT生成token 存入id和手机号码
        Map tokenMap = new HashMap();
        tokenMap.put("id",user.getId());
        tokenMap.put("mobile",phone);
        String token = JwtUtils.getToken(tokenMap);

        Map retMap = new HashMap();
        retMap.put("token",token);
        retMap.put("isNew",isNew);

        return retMap;
    }
}
