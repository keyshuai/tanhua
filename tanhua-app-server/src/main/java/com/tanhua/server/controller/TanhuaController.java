package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.NearUserVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    //推荐用户列表
    @GetMapping("/cards")
    public ResponseEntity cards(){
        List<TodayBest> list=this.tanhuaService.cards();
        return ResponseEntity.ok(list);
    }
    //喜欢
    @GetMapping("{id}/love")
    public ResponseEntity<Void> likeUser(@PathVariable("id")Long likeUserId){
        this.tanhuaService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    @GetMapping("{id}/unlove")
    public ResponseEntity<Void> notLikeUser(@PathVariable("id")Long likeUserId){
        this.tanhuaService.notLikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/search")
    public ResponseEntity queryNearUser(String gender,
                                        @RequestParam(defaultValue = "2000")String distance){
        List<NearUserVo> list=tanhuaService.queryNearUser(gender,distance);
        return ResponseEntity.ok(list);
    }
}
