package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
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
    public ResponseEntity movements(Movement movement, MultipartFile imageContent[]) throws IOException {
        movementsService.publishMovement(movement, imageContent);
        return ResponseEntity.ok(null);

    }

    //查询动态
    @GetMapping("/all")
    public ResponseEntity findByUserId(Long userId,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        //分页
        PageResult pr = movementsService.findByUserId(userId, page, pageSize);
        return ResponseEntity.ok(pr);
    }

    //查询好友动态
    @GetMapping
    public ResponseEntity movements(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findFriendMovements(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    //
    @GetMapping("/recommend")
    public ResponseEntity recommend(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findRecommendMovement(page, pagesize);
        return ResponseEntity.ok(pr);

    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") String movementId) {
        MovementsVo vo = movementsService.findMovementById(movementId);
        return ResponseEntity.ok(vo);

    }
}