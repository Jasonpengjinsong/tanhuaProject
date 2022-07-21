package com.halead.server.controller;

import com.halead.dubbo.pojo.Video;
import com.halead.server.service.VideoMqService;
import com.halead.server.service.VideoService;
import com.halead.server.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @title: VideoController
 * @Author ppjjss
 * @Date: 2022/7/3 12:35
 * @Version 1.0
 */
@RestController
@RequestMapping("smallVideos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoMqService videoMqService;

    @Autowired
    private MovementsController movementsController;

    @Autowired
    private CommentsController commentsController;

    /**
     * 发布小视频
     * @param picFile
     * @param videoFile
     * @return
     */
    @PostMapping
    public ResponseEntity<Video> saveVideo(@RequestParam(value = "videoThumbnail",required = false)MultipartFile picFile,
                                           @RequestParam(value = "videoFile",required = false) MultipartFile videoFile
                                           ){
        try {
            String id= this.videoService.saveVideo(picFile,videoFile);
            if(StringUtils.isNotEmpty(id)){
                //TODO 发送消息
                //this.videoMqService.videoMsg(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询小视频列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResult> queryVideoList(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                     @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize
                                                     ){
        if(page<0){
            page=1;
        }
        try {
            PageResult pageResult=  this.videoService.queryVideoList(page,pageSize);
            if(null != pageResult){
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    /**
     * 视频点赞
     *
     * @param videoId 视频id
     * @return
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Long> likeComment(@PathVariable("id") String videoId) {
        ResponseEntity<Long> entity = this.movementsController.likeComment(videoId);
        if(entity.getStatusCode().is2xxSuccessful()){
            //TODO 发送消息
            //this.videoMQService.likeVideoMsg(videoId);
        }
        return entity;
    }

    /**
     * 取消点赞
     *
     * @param videoId
     * @return
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity<Long> disLikeComment(@PathVariable("id") String videoId) {

        ResponseEntity<Long> entity = this.movementsController.dislikeComment(videoId);

        if(entity.getStatusCode().is2xxSuccessful()){
            //TODO 发送消息
            //this.videoMQService.disLikeVideoMsg(videoId);
        }

        return entity;
    }

    /**
     * 评论列表
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<PageResult> queryCommentsList(@PathVariable("id") String videoId,
                                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize) {
        return this.commentsController.queryCommentsList(videoId, page, pagesize);
    }

    /**
     * 提交评论
     *
     * @param param
     * @param videoId
     * @return
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> saveComments(@RequestBody Map<String, String> param,
                                             @PathVariable("id") String videoId) {
        param.put("movementId", videoId);

        ResponseEntity<Void> entity = this.commentsController.saveComments(param);

        if(entity.getStatusCode().is2xxSuccessful()){
            // TODO 发送消息
           // this.videoMQService.commentVideoMsg(videoId);
        }
        return entity;
    }

    /**
     * 评论点赞
     *
     * @param publishId
     * @return
     */
    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Long> commentsLikeComment(@PathVariable("id") String publishId) {
        return this.movementsController.likeComment(publishId);
    }
    /**
     * 评论取消点赞
     *
     * @param publishId
     * @return
     */
    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity<Long> disCommentsLikeComment(@PathVariable("id") String publishId) {
        return this.movementsController.dislikeComment(publishId);
    }
    /**
     * 视频用户关注
     */
    @PostMapping("/{id}/userFocus")
    public ResponseEntity<Void> saveUserFocusComments(@PathVariable("id") Long userId) {
        try {
            Boolean bool = this.videoService.followUser(userId);
            if (bool) {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    /**
     * 取消视频用户关注
     */
    @PostMapping("/{id}/userUnFocus")
    public ResponseEntity<Void> saveUserUnFocusComments(@PathVariable("id") Long userId) {
        try {
            Boolean bool = this.videoService.disFollowUser(userId);
            if (bool) {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}