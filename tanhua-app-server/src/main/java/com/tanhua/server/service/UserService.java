package com.tanhua.server.service;

import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private SmsTemplate template;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @DubboReference
    private UserApi userApi;

    //发送验证码
    public void sendMsg(String phone) {
        //1、随机生成6位数字
        //String code = RandomStringUtils.randomNumeric(6);
        String code = "123456";
        //2、调用template对象，发送手机短信
        //template.sendSms(phone,code);
        //3、将验证码存入到redis
        redisTemplate.opsForValue().set("CHECK_CODE_"+phone,code, Duration.ofMinutes(5));
    }

    //验证登录
    public Map loginVerification(String phone, String code) {
        String redisCode = redisTemplate.opsForValue().get("CHECK_CODE_" + phone);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)){
            //验证码无效
            throw new RuntimeException();
        }
        redisTemplate.delete("CHECK_CODE_"+phone);
        User user = userApi.findByMobile(phone);
        boolean isNew = false;
        if (user ==null){
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long userId=userApi.save(user);
            user.setId(userId);
            isNew=true;
        }
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
