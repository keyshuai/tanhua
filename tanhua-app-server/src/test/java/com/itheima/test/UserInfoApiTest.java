package com.itheima.test;

import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class UserInfoApiTest {

    @DubboReference
    private UserInfoApi userInfoApi;

    /*
        用户详情
     */
    @Test
    public void testFindByIds() {
        List<Long> ids = new ArrayList<Long>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        ids.add(4L);
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(35);

        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);

        map.forEach((k, v) -> System.out.println(k + "--" + v));
    }
}
