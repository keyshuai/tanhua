package com.tanhua.server.controller;

import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map){
        String phone = (String) map.get("phone");
        userService.sendMsg(phone);
        return ResponseEntity.ok("成功");
    }

    @PostMapping("loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map){
        //1、调用map集合获取请求参数
        String phone = (String) map.get("phone");
        String code = (String) map.get("verificationCode");
        //2、调用userService完成用户登录
        Map retMap = userService.loginVerification(phone,code);
        //3、构造返回
        return ResponseEntity.ok(retMap);
    }
}
