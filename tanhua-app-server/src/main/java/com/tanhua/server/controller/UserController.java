package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo,@RequestHeader("Authorization")String token){


        userInfo.setId(UserHolder.getUserId());
        userInfoService.save(userInfo);
        return ResponseEntity.ok(null);
    }




    @PostMapping("/loginReginfo/head")
    public ResponseEntity updatuserInfo(MultipartFile headPhoto, @RequestHeader("Authorization")String token) throws IOException {

        userInfoService.updateHead(headPhoto,UserHolder.getUserId());
        return  ResponseEntity.ok("修改完成");
    }

}
