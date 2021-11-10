package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo,@RequestHeader("Authorization")String token){
        Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");
        userInfo.setId(Long.valueOf(id));
        userInfoService.save(userInfo);
        return ResponseEntity.ok("保存完成");
    }




    @PutMapping("loginReginfo/head")
    public ResponseEntity updatuserInfo(MultipartFile headPhoto, @RequestHeader("Authorization")String token) throws IOException {
        Claims claims = JwtUtils.getClaims(token);
        Long id= (Long) claims.get("id");
        userInfoService.updateHead(headPhoto,id);
        return  ResponseEntity.ok("修改完成");
    }

}
