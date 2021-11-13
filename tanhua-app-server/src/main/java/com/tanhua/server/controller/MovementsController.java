package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.MovementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/movements")
public class MovementsController {
    @Autowired
    private MovementsService movementsService;

    //发布动态
    @PostMapping
    public ResponseEntity movements(Movement movement , MultipartFile imageContent[]) throws IOException {
        movementsService.publishMovement(movement,imageContent);
        return ResponseEntity.ok(null);

    }
    //查询动态
    @GetMapping("/all")
    public ResponseEntity findByUserId(Long userId,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize){
        //分页
        PageResult pr =movementsService.findByUserId(userId,page,pageSize);
        return ResponseEntity.ok(pr);

    }

}
