package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.mappers.BlackListMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DubboService
public class BlackListServiceImpl implements BlackListApi{

    @Autowired
    private BlackListMapper blackListMapper;
    @Override
    public IPage<UserInfo> findByUserId(Long userId, int page, int size) {
        return null;
    }

    @Override
    public void delete(Long userId, Long blackUserId) {

    }
}
