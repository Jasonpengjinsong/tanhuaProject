package com.halead.server.controller;

import com.halead.server.service.IMService;
import com.halead.server.utils.NoAuthorization;
import com.halead.server.vo.PageResult;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Logger;

import static org.slf4j.LoggerFactory.*;

/**
 * @title: IMcontroller
 * @Author ppjjss
 * @Date: 2022/7/3 20:40
 * @Version 1.0
 */
@RestController
@RequestMapping("messages")
public class IMController {

   // private static final Logger LOGGER = getLogger(IMController.class);

    @Autowired
    private IMService imService;


    /**
     * 添加联系人
     * @param param
     * @return
     */
    @PostMapping("contacts")
    public ResponseEntity<Void> contactUser(@RequestBody Map<String,Object> param){

        try {
            Long userId= Long.valueOf(param.get("userId").toString());
            Boolean result= this.imService.contactUser(userId);
            if(result){
                return ResponseEntity.ok(null);
            }
        } catch (NumberFormatException e) {
          // LOGGER.error("添加联系人失败~ param = " + param, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询联系人列表
     * @param page
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("contacts")
    public ResponseEntity<PageResult> queryContactsList(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                        @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                                        @RequestParam(value = "keyword",required = false) String keyword
                                                        ){
       PageResult pageResult= this.imService.queryContactsList(page,pageSize,keyword);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询点赞列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("likes")
    public ResponseEntity<PageResult> queryMessageLikeList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                           @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = this.imService.queryMessageLikeList(page, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询评论列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("comments")
    public ResponseEntity<PageResult> queryMessageCommentList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = this.imService.queryMessageCommentList(page, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询公告列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("announcements")
    @NoAuthorization
    public ResponseEntity<PageResult> queryMessageAnnouncementList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                   @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = this.imService.queryMessageAnnouncementList(page, pageSize);
        return ResponseEntity.ok(pageResult);
    }

}
