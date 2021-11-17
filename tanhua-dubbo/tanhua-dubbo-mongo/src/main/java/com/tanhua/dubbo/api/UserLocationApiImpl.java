package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.util.List;

@DubboService
public class UserLocationApiImpl implements UserLocationApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            //根据id查询地理位置
            Query query = Query.query(Criteria.where("userId").is(userId));
            UserLocation location = mongoTemplate.findOne(query, UserLocation.class);
            if (location==null){
                //如果不存在用户位置消息
                location=new UserLocation();
                location.setUserId(userId);
                location.setAddress(address);
                location.setCreated(System.currentTimeMillis());
                location.setUpdated(System.currentTimeMillis());
                location.setLastUpdated(System.currentTimeMillis());
                mongoTemplate.save(location);
            }else {
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", location.getUpdated());
                mongoTemplate.updateFirst(query,update,UserLocation.class);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Long> queryNearUser(Long userId, Double metre) {
        //根据用户查询位置信息
        Query query = Query.query(Criteria.where("userId").is(userId));
        UserLocation location = mongoTemplate.findOne(query, UserLocation.class);
        if (location==null){
            return null;
        }
        //给当前用户位置绘制原点
        GeoJsonPoint point = location.getLocation();
        //绘制半径
        Distance distance = new Distance(metre / 1000, Metrics.KILOMETERS);
        //绘制圆形
        Circle circle = new Circle(point, distance);
        //查询
        Query locationQuery = Query.query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> list = mongoTemplate.find(locationQuery, UserLocation.class);
        return CollUtil.getFieldValues(list,"userId",Long.class);
    }
}
