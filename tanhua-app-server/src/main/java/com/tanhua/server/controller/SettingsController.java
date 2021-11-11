package com.tanhua.server.controller;

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
}
