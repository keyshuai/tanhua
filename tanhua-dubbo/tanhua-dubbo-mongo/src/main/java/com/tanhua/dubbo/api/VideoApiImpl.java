package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.util.List;

@DubboService
public class VideoApiImpl implements VideoApi{

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private VideoApi videoApi;

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
        Query query = Query.query(Criteria.where("vid").in(vids));
        return mongoTemplate.find(query,Video.class);
    }

    @Override
    public List<Video> queryVideoList(int page, Integer pagesize) {
        Query query = new Query().limit(pagesize).skip((page -1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query,Video.class);
    }

    @Override
    public PageResult findByUserId(Integer page, Integer pagesize, Long userId) {
        //总数
        Query query = Query.query(Criteria.where("userId").in(userId));
        long count = mongoTemplate.count(query, Video.class);
        //分页列表
        query.limit(pagesize).skip((page-1)*pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        //数据列表
        List<Video> list = mongoTemplate.find(query, Video.class);
        return new PageResult(page,pagesize,count,list);
    }

    @Override
    public void like(Long id) {
        //设置被评论人属性
        Query query=Query.query(Criteria.where("Id").is(id));
        Update update=new Update();
        update.inc("likeCount",1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);


        Video modify = mongoTemplate.findAndModify(query, update, options, Video.class);

    }
}
