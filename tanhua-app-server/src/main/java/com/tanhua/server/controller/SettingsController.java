package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;
    //查询通用设置
    @GetMapping("/settings")
    public ResponseEntity settings(){
        SettingsVo vo = settingsService.settings();
        return ResponseEntity.ok(vo);
    }

    @PostMapping("/questions")
    public ResponseEntity questions(@RequestBody Map map){
        String content = (String) map.get("content");
        settingsService.saveQuestion(content);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/notifications/setting")
    public ResponseEntity notifications(@RequestBody Map map){
        settingsService.saveSettings(map);
        return ResponseEntity.ok(null);
    }
    //分页查询黑名单
    @GetMapping("/blacklist")
    public ResponseEntity blacklist(@RequestParam(defaultValue = "1")int page,@RequestParam(defaultValue = "10")int size){
        PageResult pr=settingsService.blacklist(page,size);
        return ResponseEntity.ok(pr);
    }
    //取消黑名单
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity deleteBlackList(@PathVariable("uid")Long blackUserId){
        settingsService.deleteBlackList(blackUserId);
        return ResponseEntity.ok("取消黑名单成功");
    }
}
