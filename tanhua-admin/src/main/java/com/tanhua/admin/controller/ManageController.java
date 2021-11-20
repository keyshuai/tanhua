package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManageService;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/manage")
public class ManageController {

    @Autowired
    private ManageService manageService;

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
}
