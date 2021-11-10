package com.itheima.test;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class MovementApiTest {

    private String[] contents = new String[]{
            "记住：现在是你生命中最好的年纪。",
            "习惯了任何人的忽冷忽热，看淡任何人的渐行渐远。",
            "时间几乎会愈合所有伤口，如果你的伤口还没有愈合，请给时间一点时间。",
            "别说什么木克水，水克土，土克火，火克金，只要你穷，什么都会克你。",
            "这世上没有人是不可或缺的，没有什么是不可替代的。",
            "时间从来不语，却回答了所有问题。",
            "光阴似箭，时光荏苒，一转眼一年就过去了。",
            "人生就是见自己，见天地，见众生的过程。",
            "我们不必要委屈自己去迎合别人，不开心就说，大不了就少认识一个人而已。",
            "别人都羡慕你自在如风，只有你知道自己无依无靠。",
            "人生的路，虽然难走，但是没有绝境，只要寻找，总有路可走。"
    };

    @DubboReference
    private MovementApi movementApi;

    /*
        圈子测试
     */
    @Test
    public void testPublish() {

        String images = "https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/group_{}.png";

        List<String> list = Arrays.asList(new String[]{"1", "2", "3", "4", "5", "6"});

        for (String content : contents) {
            Collections.shuffle(list);
            List<String> medias = list.stream().limit(3).map(e -> StrUtil.format(images, e)).collect(Collectors.toList());
            Movement movement = new Movement();
            movement.setUserId(RandomUtil.randomLong(2L, 6L)); //用户IDW
            movement.setTextContent(content);
            movement.setMedias(medias);
            movement.setState(1);
            movement.setLatitude("40.066355");
            movement.setLongitude("116.350426");
            movement.setLocationName("中国北京市昌平区建材城西路16号");
            movementApi.publish(movement);
        }
    }

    @Test
    public void findFriendMovements() {
        List<Movement> movementList = movementApi.findFriendMovements(1, 5, 1L);
        movementList.forEach(System.out::println);
    }

}