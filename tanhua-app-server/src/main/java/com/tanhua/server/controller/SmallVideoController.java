package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentService;
import com.tanhua.server.service.SmallVideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideoController {

    @Autowired
    private SmallVideosService videosService;

    @Autowired
    private CommentService commentService;

    /**
     * 发布视频
     *  接口路径：POST
     *  请求参数：
     *      videoThumbnail：封面图
     *      videoFile：视频文件
     */
    @PostMapping
    public ResponseEntity saveVideo(MultipartFile videoThumbnail,MultipartFile videoFile) throws IOException {
        videosService.save(videoThumbnail,videoFile);
        return ResponseEntity.ok("上传成功");
    }
    //查询视频列表
    @GetMapping
    public ResponseEntity saveVideo(@RequestParam(defaultValue = "1")Integer page,@RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pr=videosService.getSave(page,pagesize);
        return ResponseEntity.ok(pr);
    }

    //视频关注
    @PostMapping("/{uid}/userFocus")
    public ResponseEntity userFocus(@PathVariable("uid") String id){
        Integer userFcous=videosService.userFocus(id);
        System.out.println(id);
        return ResponseEntity.ok(userFcous);
    }

    //视频取消关注
    @PostMapping("/{uid}/userUnFocus")
    public ResponseEntity userUnFocus(@PathVariable("uid") String id){
        Integer userUnFocus=videosService.userUnFocus(id);
        return ResponseEntity.ok(userUnFocus);
    }


    //视频点赞
    @PostMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String id){
        Integer like=videosService.like(id);
        return ResponseEntity.ok(like);
    }
    //取消点赞
    @PostMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id")String id){
        Integer dislike=videosService.dislike(id);
        return ResponseEntity.ok(dislike);
    }
    //视频评论列表
    @GetMapping("/{id}/comments")
    public ResponseEntity commentsFind(@PathVariable("id")String id,
                                       @RequestParam(defaultValue = "1")Integer page,
                                       @RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pr=videosService.commentsFind(id,page,pagesize);
        return ResponseEntity.ok(pr);

    }

    //视频评论发布
    @PostMapping("/{id}/comments")
    public ResponseEntity comments(@RequestBody Map map){
        String comment = (String) map.get("comment");
        String id = (String) map.get("id");

        commentService.saves(id,comment);
        return ResponseEntity.ok(null);
    }
    //评论点赞
    @PostMapping("/comments/{id}/like")
    public ResponseEntity commentsLike(@PathVariable("id")String id){
        Integer like=commentService.commentsLike(id);
        return ResponseEntity.ok(like);
    }
    //评论取消点赞
    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity commentDisLike(@PathVariable("id")String id){
        Integer dislike=commentService.commentsdisLike(id);
        return ResponseEntity.ok(dislike);
    }


}
