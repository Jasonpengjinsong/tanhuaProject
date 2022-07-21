package com.halead.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.halead.dubbo.api.QuanZiApi;
import com.halead.dubbo.api.VideoApi;
import com.halead.dubbo.pojo.Video;
import com.halead.dubbo.vo.PageInfo;
import com.halead.server.pojo.User;
import com.halead.server.pojo.UserInfo;
import com.halead.server.utils.UserThreadLocal;
import com.halead.server.vo.PageResult;
import com.halead.server.vo.PicUploadResult;
import com.halead.server.vo.VideoVo;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: VideoService
 * @Author ppjjss
 * @Date: 2022/7/3 12:36
 * @Version 1.0
 */
@Service
public class VideoService {

    @Autowired
    private PicUploadService picUploadService;
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private FdfsWebServer fdfsWebServer;
    @Reference
    private VideoApi videoApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private QuanZiApi quanZiApi;
    @Autowired
    private UserInfoService userInfoService;

    public String saveVideo(MultipartFile picFile, MultipartFile videoFile) {
        User user = UserThreadLocal.get();

        Video video = new Video();
        video.setUserId(user.getId());
        video.setSeeType(1);
        try {
            //上传图片
            PicUploadResult picUploadResult = this.picUploadService.upload(picFile);
            video.setVideoUrl(picUploadResult.getName());
            //上传视频
            StorePath storePath = storageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), StringUtils.substringAfter(videoFile.getOriginalFilename(), "."), null);
            video.setVideoUrl(fdfsWebServer.getWebServerUrl() + storePath.getFullPath());
            return this.videoApi.saveVideo(video);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PageResult queryVideoList(Integer page, Integer pageSize) {
        User user = UserThreadLocal.get();
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        //先从redis中进行命中，如果命中则返回推荐列表，如果没有命中查询默认列表
        PageInfo<Video> pageInfo = null;
        String redisValue = this.redisTemplate.opsForValue().get("QUANZI_VIDEO_RECOMMENT_" + user.getId());
        if(StringUtils.isNotEmpty(redisValue)){
            String[] vids = StringUtils.split(redisValue, ",");
            int startIndex =(page -1)*pageSize;
            if(startIndex <vids.length){
                int endIndex =startIndex+pageSize -1;
                if(endIndex >= vids.length){
                    endIndex =vids.length -1;
                }
                ArrayList<Long> vidList = new ArrayList<>();
                for (int i = startIndex; i <= startIndex; i++) {
                    vidList.add(Long.valueOf(vids[i]));
                }
                List<Video> videosList = this.videoApi.queryVideoListByPids(vidList);
               pageInfo= new PageInfo<>();
               pageInfo.setRecords(videosList);
            }
        }

        if(null ==pageInfo){
           pageInfo= this.videoApi.queryVideoList(page,pageSize);
        }
        List<Video> records = pageInfo.getRecords();
        List<VideoVo> videoVoList = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();

        for (Video record : records) {
            VideoVo videoVo = new VideoVo();

            videoVo.setUserId(record.getUserId());
            videoVo.setCover(record.getPicUrl());
            videoVo.setVideoUrl(record.getVideoUrl());
            videoVo.setId(record.getId().toHexString());
            videoVo.setSignature("我就是我~"); //TODO 签名

            Long commentCount = this.quanZiApi.queryCommentCount(videoVo.getId(), 2);
            videoVo.setCommentCount(commentCount == null ? 0 : commentCount.intValue()); // 评论数

            videoVo.setHasFocus(0); //TODO 是否关注

            String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + videoVo.getId();
            videoVo.setHasLiked(this.redisTemplate.hasKey(likeUserCommentKey) ? 1 : 0); //是否点赞

            String likeCommentKey = "QUANZI_COMMENT_LIKE_" + videoVo.getId();
            String value = this.redisTemplate.opsForValue().get(likeCommentKey);
            if(StringUtils.isNotEmpty(value)){
                videoVo.setLikeCount(Integer.valueOf(value)); //点赞数
            }else{
                videoVo.setLikeCount(0); //点赞数
            }

            if (!userIds.contains(record.getUserId())) {
                userIds.add(record.getUserId());
            }

            videoVoList.add(videoVo);
        }


        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper();
        queryWrapper.in("user_id", userIds);
        List<UserInfo> userInfos = this.userInfoService.queryUserInfoList(queryWrapper);
        for (VideoVo videoVo : videoVoList) {
            for (UserInfo userInfo : userInfos) {
                if (videoVo.getUserId().longValue() == userInfo.getUserId().longValue()) {

                    videoVo.setNickname(userInfo.getNickName());
                    videoVo.setAvatar(userInfo.getLogo());

                    break;
                }
            }

        }

        pageResult.setItems(videoVoList);

        return pageResult;
    }

    public Boolean followUser(Long userId) {
        User user = UserThreadLocal.get();
        this.videoApi.followUser(user.getId(),userId);
        //记录已关注
        String followUserKey = "VIDEO_FOLLOW_USER_"+user.getId()+"_"+userId;
        this.redisTemplate.opsForValue().set(followUserKey,"1");

        return true;
    }

    public Boolean disFollowUser(Long userId) {
        User user = UserThreadLocal.get();
        this.videoApi.disFollowUser(user.getId(),userId);

        String followUserKey = "VIDEO_FOLLOW_USER_"+user.getId()+"_"+userId;
        this.redisTemplate.delete(followUserKey);
        return true;
    }
}
