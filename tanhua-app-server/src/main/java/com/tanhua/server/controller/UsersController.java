package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.CountsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserInfoService userInfoService;


    //查询用户资料
    @GetMapping
    public ResponseEntity users(@RequestHeader("Authorization")String token,Long userID){

        //判断用户是否为空
        if (userID == null){
            userID = UserHolder.getUserId();
        }
        UserInfoVo userInfoVo=userInfoService.findById(userID);
        return ResponseEntity.ok(userInfoVo);
    }
    //更新用户资料
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo,@RequestHeader("Authorization")String token){


        userInfo.setId(UserHolder.getUserId());
        userInfoService.update(userInfo);
        return ResponseEntity.ok("更新完成");
    }

    @GetMapping("/counts")
    public ResponseEntity<CountsVo> counts(){
        CountsVo countsVo=userInfoService.counts();
        return ResponseEntity.ok(countsVo);

    }

    @GetMapping("/friends/{type}")
    public ResponseEntity friends(@PathVariable("type") String type,
                                  @RequestParam(defaultValue = "1")Integer page,
                                  @RequestParam(defaultValue = "10")Integer pagesize,
                                  String nickname){
        PageResult pr=userInfoService.friends(Integer.valueOf(type),page,pagesize,nickname);
        return ResponseEntity.ok(pr);
    }

}
