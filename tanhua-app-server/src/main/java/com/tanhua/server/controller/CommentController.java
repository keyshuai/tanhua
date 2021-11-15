package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    //发布评论
    @PostMapping
    public ResponseEntity save(@RequestBody Map map){
        String movementId= (String) map.get("movementId");
        String comment= (String) map.get("comment");
        commentService.save(movementId,comment);
        return ResponseEntity.ok("保存成功");
    }

    //分页查询评论列表
    @GetMapping
    public ResponseEntity findComments(@RequestParam(defaultValue = "1")Integer page,
                                       @RequestParam(defaultValue = "10")Integer pagesize,
                                       String movementId){
        PageResult p=commentService.findComments(movementId,page,pagesize);
        return ResponseEntity.ok(p);

    }
    //评论点赞
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String movementId){
        Integer like=commentService.pllike(movementId);
        return ResponseEntity.ok(like);
    }

    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String movementId){
        Integer like=commentService.displlike(movementId);
        return ResponseEntity.ok(like);
    }
}
