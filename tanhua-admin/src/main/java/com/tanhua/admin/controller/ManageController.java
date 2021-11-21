package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManageService;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;
import java.util.Map;


@RestController
@RequestMapping("/manage")
public class ManageController {

    @Autowired
    private ManageService manageService;
    //用户列表
    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1")Integer page,
                                @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pr=manageService.findAllUsers(page,pagesize);
        return ResponseEntity.ok(pr);
    }


    //根据id查询
    @GetMapping("/users/{userId}")
    public ResponseEntity findUserById(@PathVariable("userId") Long userId){
        UserInfo userInfo=manageService.findUserById(userId);
        return ResponseEntity.ok(userInfo);
    }

    //查询指定用户发布的视频
    @GetMapping("/videos")
    public ResponseEntity videos(@RequestParam(defaultValue = "1")Integer page,
                                 @RequestParam(defaultValue = "10")Integer pagesize,
                                 Long uid){

        PageResult pr=manageService.findAllVideos(page,pagesize,uid);
        return ResponseEntity.ok(pr);
    }

    //查询动态
    @GetMapping("/messages")
    public ResponseEntity messages(@RequestParam(defaultValue = "1")Integer page,
                                   @RequestParam(defaultValue = "10")Integer pagesize,
                                   @RequestParam(defaultValue = "1")Integer state,
                                   Long uid){
        PageResult pr=manageService.findAllMovements(page,pagesize,uid,state);
        return ResponseEntity.ok(pr);
    }


    @PostMapping("/users/freeze")
    public ResponseEntity freeze(@RequestBody Map params){
           Map map= manageService.userFreeze(params);
           return ResponseEntity.ok(map);
    }

    //用户解冻
    @PostMapping("/users/unfreeze")
    public ResponseEntity unfreeze(@RequestBody Map params){
        Map map =manageService.unfreeze(params);
        return ResponseEntity.ok(map);
    }




    @GetMapping("/messages/{id}")
    public ResponseEntity messagesById(@PathVariable("id") Long id){
            VisitorsVo visitorsVo=manageService.messagesById(id);
            return ResponseEntity.ok(visitorsVo);
    }
}
