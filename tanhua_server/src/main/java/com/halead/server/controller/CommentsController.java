package com.halead.server.controller;

import com.halead.server.service.CommentsService;
import com.halead.server.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.groups.Default;
import java.util.Map;

/**
 * @title: CommentsController
 * @Author ppjjss
 * @Date: 2022/7/2 23:34
 * @Version 1.0
 */
@RestController
@RequestMapping("comment")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;


    /**
     * 查询评论列表
     * @param publishId
     * @param page
     * @param pageSize
     * @return
     */
    public ResponseEntity<PageResult> queryCommentsList(@RequestParam("movementId") String publishId,
                                                        @RequestParam(value = "page",defaultValue = "1") Integer page,
                                                        @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize
                                                        ){

        try {
            PageResult pageResult= this.commentsService.queryCommentList(publishId,page,pageSize);
            if(null != pageResult){
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    /**
     * 发布评论
     * @param param
     * @return
     */
    public ResponseEntity<Void> saveComments(Map<String, String> param) {

        try {
            String publishId = param.get("movementId");
            String content = param.get("comment");
            Boolean bool= this.commentsService.saveComment(publishId,content);
            if(bool){
                // TODO 发送消息
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
