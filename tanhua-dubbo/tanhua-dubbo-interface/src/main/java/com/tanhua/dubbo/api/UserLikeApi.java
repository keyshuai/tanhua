package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface UserLikeApi {

    //保存或者更新
    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike);

    Integer querylike(Long userId);

    Integer querybean(Long userId);

    Integer queryeachlove(Long userId);

    List<Friend> friends(Long userId);

    List<UserLike> likeUserId(Long userId);

    List<UserLike> isLike(Long userId);


    UserLike find(Long aLong);
}
