package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.Admin;
import com.tanhua.model.vo.AdminVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/system/users")
public class SystemController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //获取验证码图片
    @GetMapping("/verification")
    public void verification(String uuid , HttpServletResponse response) throws IOException {
        //1.生成验证码
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(299, 97);
        //2.验证码存入redis
        String code = captcha.getCode();
        redisTemplate.opsForValue().set(Constants.CAP_CODE+uuid,code,1, TimeUnit.MINUTES);
        //输出验证码图片
        captcha.write(response.getOutputStream());
    }
    //用户登录
    @PostMapping("/login")
    public ResponseEntity login (@RequestBody Map map){
        Map retMap=adminService.login(map);
        return ResponseEntity.ok(retMap);
    }
    //用户列表
    @PostMapping("/profile")
    public ResponseEntity profile(){
        AdminVo vo=adminService.profile();
        return ResponseEntity.ok(vo);
    }

}
