package com.tanhua.recommend.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementScore;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.mongo.VideoScore;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RecommendVideoListener {
    @Autowired
    private MongoTemplate mongoTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "tanhua.video.queue",
                    durable = "true"
            ),
            exchange = @Exchange(
                    value = "tanhua.log.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"log.video"}

    ))
    public void recommend(String message){
        System.out.println("处理视频消息"+message);

        try {
            Map<String,Object>map=JSON.parseObject(message);
            //获取数据
            Long userId= (Long) map.get("userId");
            String date= (String) map.get("date");
            String objId= (String) map.get("objId");
            String type= (String) map.get("type");
            Video video = mongoTemplate.findById(objId, Video.class);
            if (video!=null){
                VideoScore vs = new VideoScore();
                vs.setUserId(userId);
                vs.setDate(System.currentTimeMillis());
                vs.setVideoId(video.getVid());
                vs.setScore(getScore(type));
                mongoTemplate.save(vs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static Double getScore(String type) {
        //0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论
        Double score = 0d;
        switch (type) {
            case "0301":
                score=2d;
                break;
            case "0302":
                score=5d;
                break;
            case "0303":
                score = -5d;
                break;
            case "0304":
                score = 10d;
                break;
            default:
                break;
        }
        return score;
    }
}
