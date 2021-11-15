package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {
    @Autowired
    private TanhuaService tanhuaService;

    @GetMapping("/todayBest")
    public ResponseEntity todayBest(){
        TodayBest vo=tanhuaService.todayBest();
        return ResponseEntity.ok(vo);
    }
    //查询分页推荐好友列表
    @GetMapping("/recommendation")
    public ResponseEntity personalInfo(RecommendUserDto dto){
        PageResult result=tanhuaService.recommendation(dto);
        return ResponseEntity.ok(result);
    }
    //查看佳人详情
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long userId){
        TodayBest best=tanhuaService.personalInfo(userId);
        return ResponseEntity.ok(best);
    }
    //查看陌生人信息
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId){
        String questions=tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(questions);
    }
    //回复陌生人信息
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map){
        String obj = map.get("userId").toString();
        Long userId = Long.valueOf(obj);
        String reply = (String) map.get("reply");
        tanhuaService.replyQuestions(userId,reply);
        return ResponseEntity.ok("null");
    }
}
