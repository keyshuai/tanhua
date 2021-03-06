package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessagesService;
import jdk.nashorn.internal.runtime.events.RecompilationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    //添加好友
    @PostMapping("/contacts")
    public ResponseEntity contacts(@RequestBody Map map){
        Long userId = Long.valueOf(map.get("userId").toString());
        messagesService.contacts(userId);
        return ResponseEntity.ok(null);

    }
    //分页显示好用吧
    @GetMapping("/contacts")
    public ResponseEntity contacts(@RequestParam(defaultValue = "1")Integer page,
                                   @RequestParam(defaultValue = "10")Integer pagesize,
                                   String keyword){
        PageResult pr=messagesService.findFriends(page,pagesize,keyword);
        return ResponseEntity.ok(pr);
    }


    //评论点赞查询
    @GetMapping("/likes")
    public ResponseEntity like(@RequestParam(defaultValue = "1")Integer page,
                               @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pr=messagesService.like(page,pagesize);
        return ResponseEntity.ok(pr);

    }
    //评论查询
    @GetMapping("/comments")
    public ResponseEntity comments(@RequestParam(defaultValue = "1")Integer page,
                                   @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pr=messagesService.comments(page,pagesize);
        return ResponseEntity.ok(pr);
    }

    @GetMapping("/loves")
    public ResponseEntity love(@RequestParam(defaultValue = "1")Integer page,
                                   @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pr=messagesService.love(page,pagesize);
        return ResponseEntity.ok(pr);
    }
}
