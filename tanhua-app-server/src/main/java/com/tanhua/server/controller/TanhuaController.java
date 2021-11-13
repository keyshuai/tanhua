package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
