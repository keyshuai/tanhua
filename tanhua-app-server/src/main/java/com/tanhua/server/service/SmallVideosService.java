package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SmallVideosService {
    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;

    @DubboReference
    private VideoApi videoApi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void save(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {

        if (videoFile.isEmpty() || videoThumbnail.isEmpty()){
            throw new BusinessException(ErrorResult.error());
        }
        //将视频上传到FastDFS,获取访问URL
        String filename = videoFile.getOriginalFilename();
        filename=filename.substring(filename.indexOf(".")+1);
        StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        String videoUrl=webServer.getWebServerUrl()+ storePath.getFullPath();
        //将封面图片上传到阿里云oss 获取访问地址
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        //构建Vidos对象
        Video video =new Video();
        video.setUserId(UserHolder.getUserId());
        video.setPicUrl(imageUrl);
        video.setVideoUrl(videoUrl);
        video.setText("我啥时候 java能精通啊");
        //调用api保存数据
        String save = videoApi.save(video);
        if (StringUtils.isEmpty(save)){
            throw new BusinessException(ErrorResult.error());
        }


    }
}
