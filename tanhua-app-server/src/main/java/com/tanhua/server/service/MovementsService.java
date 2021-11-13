package com.tanhua.server.service;

import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovementsService {

    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String>redisTemplate;

    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //判断发布动态内容是否存在
        if (StringUtils.isEmpty(movement.getTextContent())){
            throw new BusinessException(ErrorResult.contentError());
        }
        Long userId = UserHolder.getUserId();
        //将文件上传到阿里云oss,获取请求地址
        ArrayList<String> medias = new ArrayList<>();
        for (MultipartFile multipartFile : imageContent) {
            String upload = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
            medias.add(upload);
        }
        //将数据封装到Movement对象
        movement.setUserId(userId);//用户id
        movement.setMedias(medias);//媒地址内容

        movementApi.publish(movement);

    }

    //查询个人动态
    public PageResult findByUserId(Long userId, Integer page, Integer pageSize) {
        //根据用户id 调用api查询内容
        PageResult pr = movementApi.findByUserId(userId, page, pageSize);
        //获取PageResult中的item列表对象
        List<Movement> items = (List<Movement>) pr.getItems();
        if (items==null){
            return pr;
        }
        //循环数据列表
        UserInfo userInfo = userInfoApi.findById(userId);
        ArrayList<MovementsVo> vos = new ArrayList<>();
        for (Movement item : items) {
            MovementsVo vo = MovementsVo.init(userInfo, item);
            vos.add(vo);
        }
        //构建返回值
        pr.setItems(vos);
        return pr;
    }
}
