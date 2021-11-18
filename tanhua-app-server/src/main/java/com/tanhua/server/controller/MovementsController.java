package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import com.tanhua.server.service.CommentService;
import com.tanhua.server.service.MovementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movements")
public class MovementsController {
    @Autowired
    private MovementsService movementsService;

    @Autowired
    private CommentService commentService;

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

    //查询推荐动态
    @GetMapping("/recommend")
    public ResponseEntity recommend(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementsService.findRecommendMovement(page, pagesize);
        return ResponseEntity.ok(pr);

    }
    //查询单独动态
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") String movementId) {
        MovementsVo vo = movementsService.findMovementById(movementId);
        return ResponseEntity.ok(vo);
    }
    //点赞
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String movementId){
        Integer like=commentService.likeComment(movementId);
        return ResponseEntity.ok(like);
    }

    //取消点赞
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String movementId){
        Integer like=commentService.dislikeComment(movementId);
        return ResponseEntity.ok(like);
    }
    //喜欢
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable("id") String movementId){
        Integer like=commentService.loveComment(movementId);
        return ResponseEntity.ok(like);
    }

    @GetMapping("/{id}/unlove")
    public ResponseEntity unlove(@PathVariable("id") String movementId){
        Integer like=commentService.unloveComment(movementId);
        return ResponseEntity.ok(like);
    }
    //访客
    @GetMapping("/visitors")
    public ResponseEntity visitors(){
        List<VisitorsVo> list=movementsService.visitors();
        return ResponseEntity.ok(list);
    }
}
