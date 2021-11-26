package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountsVo extends UserInfo implements Serializable {

    private Boolean isLike; // 是否喜欢

    //互相喜欢
    private Integer eachLoveCount;
    //喜欢
    private Integer loveCount;
    //粉丝
    private Integer fanCount;
}
