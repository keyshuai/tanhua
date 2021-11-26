package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.PeachblossomApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Peachblossom;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PeachblossomServer {
    //从调度服务器获取，一个目标存储服务器，上传
    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;// 获取存储服务器的请求URL

    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private PeachblossomApi peachblossomApi;

    public void save(MultipartFile soundFile) throws IOException {

        //将语音上传到FastDFS,获取访问URL
        String filename = soundFile.getOriginalFilename();
        filename = filename.substring(filename.lastIndexOf(".") + 1);
        StorePath storePath = client.uploadFile(soundFile.getInputStream(), soundFile.getSize(), filename, null);
        String Voice = webServer.getWebServerUrl() + storePath.getFullPath();
        //3、构建Voice对象

        Long userId = UserHolder.getUserId();
        UserInfo userInfo = userInfoApi.findById(userId);

        Peachblossom pea= new Peachblossom();
        pea.setId(Math.toIntExact(userId));
        pea.setAvatar(userInfo.getAvatar());
        pea.setNickname(userInfo.getNickname());
        pea.setGender(userInfo.getGender());
        pea.setAge(userInfo.getAge());
        pea.setSoundUrl(Voice);//语音地址
        pea.setRemainingTimes(10);
        //4、调用API保存数据
        String video=peachblossomApi.save(pea);

    }

    public Peachblossom reception() {
        Long userId = UserHolder.getUserId();
        Peachblossom peachblossom=peachblossomApi.find(userId);
        if (peachblossom.getRemainingTimes()==0){
            throw new BusinessException(ErrorResult.error());
        }
        return peachblossom;
    }
}
