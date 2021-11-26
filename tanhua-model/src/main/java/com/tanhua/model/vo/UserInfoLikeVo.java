package com.tanhua.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoLikeVo  implements Serializable {


    @TableId(type= IdType.INPUT)
    private Integer id; //用户id
    private String avatar; //用户头像
    private String nickname; //昵称
    private String gender;//性别
    private Integer age;//年龄
    private String city;//城市
    private String education;//学历
    private Integer marriage;//婚姻状态（0未婚，1已婚
    private Integer matchRate;//匹配度
    private Boolean alreadyLove; //是否喜欢他;

    /**
     * 在vo对象中，补充一个工具方法，封装转化过程
     */

}
