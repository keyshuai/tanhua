package com.itheima.test;

import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class FriendApiTest {

    @DubboReference
    private FriendApi friendApi;

    /*
        建立好友关系测试
     */
    @Test
    public void testFriend() {
        friendApi.save(1L, 2L);
        friendApi.save(1L, 3L);
        friendApi.save(1L, 4L);
        friendApi.save(1L, 5L);

        friendApi.save(2L, 1L);
        friendApi.save(2L, 3L);
        friendApi.save(2L, 4L);
        friendApi.save(2L, 5L);
    }
}