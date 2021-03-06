package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Comment;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@DubboService
public class UserInfoApiImpl implements UserInfoApi{
    @Autowired
    private UserInfoMapper userInfoMapper;


    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo info) {
        QueryWrapper qw=new QueryWrapper();
        qw.in("id",userIds);
        if (info!=null){
            if (info.getAge()!=null){
                qw.lt("age",info.getAge());
            }
            if (!StringUtils.isEmpty(info.getGender())){
                qw.eq("gender",info.getGender());
            }
        }
        List<UserInfo> list = userInfoMapper.selectList(qw);
        //调用工具包转化为map+-
        Map<Long, UserInfo> id = CollUtil.fieldValueMap(list, "id");
        return id;
    }

    @Override
    public IPage findAll(Integer page, Integer pagesize) {
        return userInfoMapper.selectPage(new Page<UserInfo>(page,pagesize),null);
    }

    //查询 点赞 评论 喜欢列表
    @Override
    public Map<Long, UserInfo> findLikes(List<Long> userIds) {

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("id",userIds);
        List<UserInfo> list = userInfoMapper.selectList(wrapper);

        //通过hutool工具包,设置map集合键值对
        Map<Long, UserInfo> map = CollUtil.fieldValueMap(list, "id");
        return map;
    }

    @Override
    public List<UserInfo> find(List<Long> friendId) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getId,friendId);
        return userInfoMapper.selectList(wrapper);
    }

    @Override
    public List<UserInfo> likeUserId(List<Long> likeUserId) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getId,likeUserId);
        return userInfoMapper.selectList(wrapper);
    }


}
