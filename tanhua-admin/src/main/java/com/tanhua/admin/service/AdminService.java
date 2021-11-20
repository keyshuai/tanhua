package com.tanhua.admin.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import com.tanhua.model.vo.AdminVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public Map login(Map map) {
        //获取请求参数
        String username= (String) map.get("username");
        String password= (String) map.get("password");
        String verificationCode= (String) map.get("verificationCode");
        String uuid= (String) map.get("uuid");
        //校验验证码是否正确
        String key=Constants.CAP_CODE+uuid;
        String value=redisTemplate.opsForValue().get(key);

        if (StringUtils.isEmpty(value)||!verificationCode.equals(value)){
            throw new BusinessException("验证码错误");
        }
        redisTemplate.delete(key);
        //根据用户名查询管理对象Admin
        QueryWrapper<Admin> qw = new QueryWrapper<Admin>().eq("username", username);
        Admin admin = adminMapper.selectOne(qw);
        //判断admin对象是否存在,密码是否一致
        password = SecureUtil.md5(password);
        if (admin==null||!password.equals(admin.getPassword())){
            throw new BusinessException("用户名或者密码错误");
        }
        //生成token
        Map tokenMap = new HashMap<>();
        tokenMap.put("id",admin.getId());
        tokenMap.put("username",admin.getUsername());
        String token = JwtUtils.getToken(tokenMap);
        //构造返回值
        Map retMap =new HashMap();
        retMap.put("token",token);
        return retMap;
    }

    public AdminVo profile() {
        Long userId = AdminHolder.getUserId();
        Admin admin = adminMapper.selectById(userId);
        return AdminVo.init(admin);
    }
}
