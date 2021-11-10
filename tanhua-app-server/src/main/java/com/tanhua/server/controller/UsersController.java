package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.UserInfoService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserInfoService userInfoService;
    //查询用户资料
    @GetMapping
    public ResponseEntity users(Long userID, @RequestHeader("Authorization")String token){
        if (userID == null){
            Claims claims = JwtUtils.getClaims(token);
            Integer id = (Integer) claims.get("id");
            userID = Long.valueOf(id);
        }
        UserInfoVo userInfoVo=userInfoService.findById(userID);
        return ResponseEntity.ok(userInfoVo);
    }
}
