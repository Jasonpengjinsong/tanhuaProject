package com.halead.server.controller;

import com.halead.server.service.MovementsService;
import com.halead.server.service.QuanziMQService;
import com.halead.server.vo.Movements;
import com.halead.server.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @title: MovementsController
 * @Author ppjjss
 * @Date: 2022/6/30 23:00
 * @Version 1.0
 */

@RequestMapping("movements")
@RestController
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    @Autowired
    private QuanziMQService quanziMQService;

    /**
     * 发布动态
     * @param textContent
     * @param location
     * @param longitude
     * @param latitude
     * @param multipartFile
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveMovements(@RequestParam("textContent") String textContent,
                                              @RequestParam("location") String location,
                                              @RequestParam("longitude") String longitude,
                                              @RequestParam("latitude") String latitude,
                                              @RequestParam("imageContent") MultipartFile[] multipartFile) {

       String publishId = this.movementsService.saveMovements(textContent,location,longitude,latitude,multipartFile);

       if(StringUtils.isNotEmpty(publishId)){

           //TODO 发送消息
            this.quanziMQService.commentPublishMsg(publishId);
           return ResponseEntity.ok(null);
       }
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询推荐的动态信息
     * @return
     */
    @GetMapping("recommend")
    public ResponseEntity<PageResult> queryRecommendPublishList(@RequestParam(value = "page",defaultValue = "1") Integer page ,
                                                                @RequestParam(value = "pageSize",defaultValue = "10" ) Integer pageSize
                                                                ){
        try {
            PageResult pageResult=  this.movementsService.queryRecommendPublishList(page,pageSize);
            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 点赞
     * @param publishId
     * @return
     */
    @GetMapping("{id}/like")
    public ResponseEntity<Long> likeComment(@PathVariable("id") String publishId){

        try {
            Long count= this.movementsService.likeComment(publishId);
            if(null == count){

                //TODO 发送消息
                this.quanziMQService.likePublishMsg(publishId) ;
                return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 取消点赞
     * @param publishId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity<Long> dislikeComment(@PathVariable("id") String publishId){

        try {
            Long count= this.movementsService.disLikeComment(publishId);

            if(null != count){
                //TODO 发送消息
               this.quanziMQService.disLikePublishMsg(publishId);
                return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 喜欢
     * @param publishId
     * @return
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Long> loveComment(@PathVariable("id") String publishId){

        try {
            Long count= this.movementsService.loveComment(publishId);
            if(null != count){
                //TODO 发送消息
               this.quanziMQService.lovePublishMsg(publishId);
               return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询单条动态信息
     * @param publishId
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Movements> queryMovenmentsById(@PathVariable("id") String publishId){

        try {
            Movements movements= this.movementsService.queryMovementsById(publishId);
            if(null !=movements){
                //TODO 发送消息
                //this.quanziMQService.sendQueryPublish(publishId);
                //return ResponseEntity.ok(movements);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 发送动态
     * @param textContent
     * @param location
     * @param longitude
     * @param latitude
     * @param multipartFile
     * @return
     */
    public ResponseEntity<Void> savaPublish(@RequestParam("textContent") String textContent,@RequestParam("location") String location,
                                            @RequestParam("longitude") String longitude,@RequestParam("latitude") String latitude,
                                             @RequestParam(value = "imageContent",required = false) MultipartFile multipartFile
                                            ){
        try {
            String publishId= this.movementsService.savePublish(textContent,location,longitude,latitude,multipartFile);
            if(StringUtils.isNotEmpty(publishId)){
                this.quanziMQService.publishMsg(publishId);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询好友动态
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public PageResult queryPublishList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        //return this.movementsService.queryPublishList(page,pageSize,false);
        return null;
    }

}
