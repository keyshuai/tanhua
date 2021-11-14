package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
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
import java.util.*;
import java.util.stream.Collectors;

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
    //好友动态
    public PageResult findFriendMovements(Integer page,Integer pagesize) {
        Long userId = UserHolder.getUserId();
        List<Movement> list = movementApi.findFriendMovements(page,pagesize,userId);
        return getPageResult(page, pagesize, list);
    }

    //公共方法
    private PageResult getPageResult(Integer page, Integer pagesize, List<Movement> list) {
        //非空判断
        if (CollUtil.isEmpty(list)){
            return new PageResult();
        }
        //获取好友的用户id
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //根据id查询用户详情
        Map<Long, UserInfo> userMaps = userInfoApi.findByIds(userIds, null);
        //一个movement构造一个vo对象
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : list) {
            UserInfo userInfo = userMaps.get(movement.getUserId());
            if (userInfo!=null){
                MovementsVo v = MovementsVo.init(userInfo, movement);
                vos.add(v);
            }
        }
        //构造pageResult并返回
        return new PageResult(page,pagesize,0L,vos);
    }

    //查询推荐动态
    public PageResult findRecommendMovement(Integer page, Integer pagesize) {
        //从redis中获取推荐数据
        String redisKey = Constants.MOVEMENTS_RECOMMEND + UserHolder.getUserId();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //判断推荐数据是否存在
        List<Movement>list = Collections.EMPTY_LIST;
        if (StringUtils.isEmpty(redisValue)){
            //如果不存在,调用Api随机构造10条动态数据
            list=movementApi.randomMovements(pagesize);

        }else {
            String[] values = redisValue.split(",");
            //判断当前页面的起始条数是否小于数组总数
            if ((page - 1)*pagesize < values.length){
                List<Long>pids =Arrays.stream(values).skip((page-1)*pagesize).limit(pagesize)
                        .map(e->Long.valueOf(e))
                        .collect(Collectors.toList());
                //调用api根据PID数组查询动态数据
                list=movementApi.findMovementsByPids(pids);
            }
        }
        return getPageResult(page,pagesize,list);

    }

}
