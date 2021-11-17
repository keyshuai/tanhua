package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.model.mongo.UserLocation;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


@Service
public class BaiduService {

    @DubboReference
    private UserLocationApi userLocationApi;

    public void updateLocation(Double logitude, Double laitude, String addrStr) {
        //标记地理位置
        Boolean flag = userLocationApi.updateLocation(UserHolder.getUserId(), logitude, logitude, addrStr);
        if (!flag){
            throw new BusinessException(ErrorResult.error());
        }
    }
}
