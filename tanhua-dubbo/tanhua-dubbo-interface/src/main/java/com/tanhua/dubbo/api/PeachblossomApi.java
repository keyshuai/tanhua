package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Peachblossom;

public interface PeachblossomApi {
    String save(Peachblossom pea);

    Peachblossom find(Long userId);
}
