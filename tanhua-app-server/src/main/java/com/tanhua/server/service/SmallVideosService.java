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
        //??????????????????FastDFS,????????????URL
        String filename = videoFile.getOriginalFilename();
        filename = filename.substring(filename.indexOf(".") + 1);
        StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        String videoUrl = webServer.getWebServerUrl() + storePath.getFullPath();
        //?????????????????????????????????oss ??????????????????
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        //??????Vidos??????
        Video video = new Video();
        video.setUserId(UserHolder.getUserId());
        video.setPicUrl(imageUrl);
        video.setVideoUrl(videoUrl);
        video.setText("???????????? java????????????");
        //??????api????????????
        String save = videoApi.save(video);
        if (StringUtils.isEmpty(save)) {
            throw new BusinessException(ErrorResult.error());
        }
        //????????????
        mqMessageService.sendLogMessage(UserHolder.getUserId(),"0301","video",save);

    }

    //??????????????????
//    @Cacheable(
//            value = "videos",
//            key ="T(com.tanhua.server.interceptor.UserHolder)"
//    )
    public PageResult getSave(Integer page, Integer pagesize) {
        //??????redis??????
        String redisKey = Constants.VIDEOS_RECOMMEND + UserHolder.getUserId();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //??????redis??????????????????
        List<Video> list = new ArrayList<>();
        int redisPages = 0;
        //??????redis?????????????????? ??????VID????????????
        if (StringUtils.isNotEmpty(redisValue)) {
            String[] values = redisValue.split(",");
            //??????????????????????????????????????????????????????
            if (page * pagesize < values.length) {
                List<Long> vids = Arrays.stream(values).skip(page * pagesize).limit(pagesize)
                        .map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());
                //??????api??????pid??????????????????
                list = videoApi.findMovementsByVids(vids);
            }
            redisPages = PageUtil.totalPage(values.length, pagesize);
        }
        if (list.isEmpty()) {
            //page ??????????????? ????????????????????????
            list = videoApi.queryVideoList(page - redisPages, pagesize); //page =1?
        }

        //5???????????????????????????????????????id
        List<Long> userId = CollUtil.getFieldValues(list, "userId", Long.class);
        //??????????????????
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
    //??????????????????
    public Integer userUnFocus(String id) {
        Long userId = UserHolder.getUserId();
        //?????????????????????
        Boolean hasUnFocus =commentApi.hasComment(id,userId, CommentType.ATTENTION);
        //???????????????????????????
        if (hasUnFocus){
            throw new BusinessException(ErrorResult.error());
        }
        //??????API???????????????mongdb
        Comment comment=new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(CommentType.ATTENTION.getType());
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        Integer count=commentApi.saves(comment);
        //??????redis???key,?????????????????????redis
        String key=Constants.FOCUS_USER_KEY+id;
        String hashKey=Constants.MOVEMENT_LOVE_HASHKEY+userId;
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;
    }
    //????????????
    public Integer userFocus(String id) {
//        Long userId = UserHolder.getUserId();
//        //?????????????????????
//        Boolean hasUnFocus =commentApi.hasComment(id,userId, CommentType.ATTENTION);
//        //???????????????????????????
//        if (hasUnFocus){
//            throw new BusinessException(ErrorResult.error());
//        }
//        //??????API???????????????mongdb
//        Comment comment=new Comment();
//        comment.setPublishId(new ObjectId(id));
//        comment.setCommentType(CommentType.ATTENTION.getType());
//        comment.setUserId(userId);
//        comment.setCreated(System.currentTimeMillis());
//        Integer count=commentApi.saves(comment);
//        //??????redis???key,?????????????????????redis
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
        comment.setCreated(System.currentTimeMillis());//??????
        Integer count =commentApi.videoSave(comment);
        String key = Constants.MOVEMENTS_INTERACT_KEY + id;
        String hashKey=Constants.MOVEMENT_LIKE_HASHKEY +UserHolder.getUserId();
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;
    }

    public Integer dislike(String id) {
        Long userId = UserHolder.getUserId();
        //??????????????????
        Boolean hasComment = commentApi.hasComment(id, userId, CommentType.LIKES);
        if (!hasComment){
            throw new BusinessException(ErrorResult.error());
        }
        //??????api????????????
        Comment comment=new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(CommentType.LIKES.getType());
        comment.setUserId(userId);
        Integer count=commentApi.deleteVideo(comment);
        //??????redis???key
        String key = Constants.MOVEMENTS_INTERACT_KEY + id;
        String hashKey=Constants.MOVEMENT_LIKE_HASHKEY +UserHolder.getUserId();
        redisTemplate.opsForHash().delete(key,hashKey,"1");
        return count;
    }
    //??????????????????
    public PageResult commentsFind(String id, Integer page, Integer pagesize) {
        List<Comment> list = commentApi.findComments(id, CommentType.COMMENT, page, pagesize);
        //??????List??????????????????
        if (CollUtil.isEmpty(list)){
            return new PageResult();
        }
        //??????????????????userinfoApi??????????????????
        List<Long> userId = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userId, null);
        //??????vo??????
        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : list) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (userInfo!=null){
                CommentVo vo = CommentVo.init(userInfo, comment);
                //??????redis ???key ????????????
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
