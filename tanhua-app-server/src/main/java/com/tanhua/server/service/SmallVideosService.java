package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {
    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private CommentApi commentApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MqMessageService mqMessageService;

    public void save(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {

        if (videoFile.isEmpty() || videoThumbnail.isEmpty()) {
            throw new BusinessException(ErrorResult.error());
        }
        //将视频上传到FastDFS,获取访问URL
        String filename = videoFile.getOriginalFilename();
        filename = filename.substring(filename.indexOf(".") + 1);
        StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        String videoUrl = webServer.getWebServerUrl() + storePath.getFullPath();
        //将封面图片上传到阿里云oss 获取访问地址
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        //构建Vidos对象
        Video video = new Video();
        video.setUserId(UserHolder.getUserId());
        video.setPicUrl(imageUrl);
        video.setVideoUrl(videoUrl);
        video.setText("我啥时候 java能精通啊");
        //调用api保存数据
        String save = videoApi.save(video);
        if (StringUtils.isEmpty(save)) {
            throw new BusinessException(ErrorResult.error());
        }
        //发送消息
        mqMessageService.sendLogMessage(UserHolder.getUserId(),"0301","video",save);

    }

    //查询视频列表
//    @Cacheable(
//            value = "videos",
//            key ="T(com.tanhua.server.interceptor.UserHolder)"
//    )
    public PageResult getSave(Integer page, Integer pagesize) {
        //查询redis数据
        String redisKey = Constants.VIDEOS_RECOMMEND + UserHolder.getUserId();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //判断redis数据是否存在
        List<Video> list = new ArrayList<>();
        int redisPages = 0;
        //判断redis数据是否存在 根据VID查询数据
        if (StringUtils.isNotEmpty(redisValue)) {
            String[] values = redisValue.split(",");
            //判断当前页的起始条数是否小于数组总数
            if (page * pagesize < values.length) {
                List<Long> vids = Arrays.stream(values).skip(page * pagesize).limit(pagesize)
                        .map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());
                //调用api根据pid查询动态数据
                list = videoApi.findMovementsByVids(vids);
            }
            redisPages = PageUtil.totalPage(values.length, pagesize);
        }
        if (list.isEmpty()) {
            //page 的计算规则 传入页码查询总数
            list = videoApi.queryVideoList(page - redisPages, pagesize); //page =1?
        }

        //5、提取视频列表中所有的用户id
        List<Long> userId = CollUtil.getFieldValues(list, "userId", Long.class);
        //查询用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userId, null);
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo info = map.get(video.getUserId());
            if (info != null) {
                VideoVo vo = VideoVo.init(info, video);
                vos.add(vo);
            }
        }
        return new PageResult(page, pagesize, 0L, vos);


    }
    //视频取消关注
    public Integer userUnFocus(String id) {
        Long userId = UserHolder.getUserId();
        //查询视频是关注
        Boolean hasUnFocus =commentApi.hasComment(id,userId, CommentType.ATTENTION);
        //如果以关注抛出异常
        if (hasUnFocus){
            throw new BusinessException(ErrorResult.error());
        }
        //调用API保存数据到mongdb
        Comment comment=new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(CommentType.ATTENTION.getType());
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        Integer count=commentApi.saves(comment);
        //存入redis的key,将用户关注存入redis
        String key=Constants.FOCUS_USER_KEY+id;
        String hashKey=Constants.MOVEMENT_LOVE_HASHKEY+userId;
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;
    }
    //视频关注
    public Integer userFocus(String id) {
//        Long userId = UserHolder.getUserId();
//        //查询视频是关注
//        Boolean hasUnFocus =commentApi.hasComment(id,userId, CommentType.ATTENTION);
//        //如果以关注抛出异常
//        if (hasUnFocus){
//            throw new BusinessException(ErrorResult.error());
//        }
//        //调用API保存数据到mongdb
//        Comment comment=new Comment();
//        comment.setPublishId(new ObjectId(id));
//        comment.setCommentType(CommentType.ATTENTION.getType());
//        comment.setUserId(userId);
//        comment.setCreated(System.currentTimeMillis());
//        Integer count=commentApi.saves(comment);
//        //存入redis的key,将用户关注存入redis
//        String key=Constants.FOCUS_USER_KEY+id;
//        String hashKey=Constants.MOVEMENT_LOVE_HASHKEY+userId;
//        redisTemplate.opsForHash().put(key,hashKey,"1");
//        return count;
        return null;
    }


    public Integer like(String id) {
        Long userId = UserHolder.getUserId();
        Boolean hasComment = commentApi.hasComment(id, userId, CommentType.LIKES);
        if (hasComment){
            throw new BusinessException(ErrorResult.error());
        }
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(CommentType.LIKES.getType());
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());//时间
        Integer count =commentApi.videoSave(comment);
        String key = Constants.MOVEMENTS_INTERACT_KEY + id;
        String hashKey=Constants.MOVEMENT_LIKE_HASHKEY +UserHolder.getUserId();
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;
    }

    public Integer dislike(String id) {
        Long userId = UserHolder.getUserId();
        //查询是否点赞
        Boolean hasComment = commentApi.hasComment(id, userId, CommentType.LIKES);
        if (!hasComment){
            throw new BusinessException(ErrorResult.error());
        }
        //调用api删除数据
        Comment comment=new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(CommentType.LIKES.getType());
        comment.setUserId(userId);
        Integer count=commentApi.deleteVideo(comment);
        //拼接redis的key
        String key = Constants.MOVEMENTS_INTERACT_KEY + id;
        String hashKey=Constants.MOVEMENT_LIKE_HASHKEY +UserHolder.getUserId();
        redisTemplate.opsForHash().delete(key,hashKey,"1");
        return count;
    }
    //视频评论列表
    public PageResult commentsFind(String id, Integer page, Integer pagesize) {
        List<Comment> list = commentApi.findComments(id, CommentType.COMMENT, page, pagesize);
        //判断List集合是否存在
        if (CollUtil.isEmpty(list)){
            return new PageResult();
        }
        //提取用户调用userinfoApi查询用户详情
        List<Long> userId = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userId, null);
        //构建vo对象
        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : list) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (userInfo!=null){
                CommentVo vo = CommentVo.init(userInfo, comment);
                //拼接redis 的key 存入缓存
                String key=Constants.MOVEMENTS_INTERACT_KEY+comment.getId();
                String hasKey=Constants.MOVEMENT_LIKE_HASHKEY+UserHolder.getUserId();

                if (redisTemplate.opsForHash().hasKey(key,hasKey)){
                    vo.setHasLiked(1);
                }
                commentVos.add(vo);
            }
        }
        return new PageResult(page,pagesize,0L,commentVos);
    }
}
