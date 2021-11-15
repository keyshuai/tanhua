package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessagesService;
import jdk.nashorn.internal.runtime.events.RecompilationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;
    @GetMapping("/userinfo")
    //JSON格式
    public ResponseEntity userinfo(String huanxinId){
        UserInfoVo userInfoVo=messagesService.findUserInfoHuanxin(huanxinId);
        return ResponseEntity.ok(userInfoVo);
    }
}
