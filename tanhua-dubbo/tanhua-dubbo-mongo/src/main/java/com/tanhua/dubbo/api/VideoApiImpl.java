package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@DubboService
public class VideoApiImpl implements VideoApi{

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Video video) {
        //设置属性
        video.setVid(idWorker.getNextId("video"));
        video.setCreated(System.currentTimeMillis());
        //调用方法保存对象
        mongoTemplate.save(video);
        return video.getId().toHexString();
    }

    @Override
    public List<Video> findMovementsByVids(List<Long> vids) {
        return null;
    }

    @Override
    public List<Video> queryVideoList(int page, Integer pagesize) {
        return null;
    }

    @Override
    public PageResult findByUserId(Integer page, Integer pagesize, Long userId) {
        return null;
    }
}
