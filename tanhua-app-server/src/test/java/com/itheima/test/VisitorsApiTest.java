package com.itheima.test;

import cn.hutool.core.util.RandomUtil;
import com.tanhua.dubbo.api.VisitorsApi;
import com.tanhua.model.mongo.Visitors;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.RoundingMode;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class VisitorsApiTest {

    @DubboReference
    private VisitorsApi visitorsApi;

    /*
        访客测试
     */
    @Test
    public void testVisitors() {

        for (long userId = 1; userId <= 22; userId++) {

            for (long count = 1; count <= 5; count++) {
                long visitorUserId = RandomUtil.randomLong(1L, 22L);
                if (userId != visitorUserId) {
                    Visitors visitors = new Visitors();
                    visitors.setUserId(userId);
                    visitors.setVisitorUserId(visitorUserId);
                    visitors.setDate(System.currentTimeMillis());
                    visitors.setFrom("个人首页");
                    visitors.setScore(RandomUtil.randomDouble(50, 100, 0, RoundingMode.FLOOR));
                    this.visitorsApi.save(visitors);
                }
            }
        }
    }

}