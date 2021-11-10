package com.itheima.test;

import cn.hutool.core.util.StrUtil;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.mongo.Video;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class VedioApiTest {

    @DubboReference
    private VideoApi videoApi;

    /*
        小视频测试
     */
    @Test
    public void testPublish() {

        String images = "https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/video/video_{}.png";

        for (long userId = 1; userId < 10; userId++) {
            Video video = new Video();
            video.setUserId(userId);
            video.setVideoUrl("http://192.168.136.160:8888/group1/M00/00/00/wKiIoGFtFJ2Adg3MACYJuhDRF3g056.mp4");
            video.setPicUrl(StrUtil.format(images, userId));
            video.setText("传智教育IPO...");
            videoApi.save(video);
        }
    }

}