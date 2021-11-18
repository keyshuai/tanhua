package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.SmallVideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideoController {

    @Autowired
    private SmallVideosService videosService;

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

    @GetMapping
    public ResponseEntity saveVideo(@RequestParam(defaultValue = "1")Integer page,@RequestParam(defaultValue = "10")Integer pagesize){
        PageResult pr=videosService.getSave(page,pagesize);
        return ResponseEntity.ok(pr);
    }

    @PostMapping("/{uid}/userUnFocus")
    public ResponseEntity userUnFocus(@RequestParam("uid") String id){
        videosService.userUnFocus(id);
        return ResponseEntity.ok("null");
    }

}
