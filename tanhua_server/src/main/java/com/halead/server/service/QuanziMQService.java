package com.halead.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.halead.dubbo.api.QuanZiApi;
import com.halead.dubbo.pojo.Publish;
import com.halead.server.pojo.User;
import com.halead.server.utils.UserThreadLocal;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @title: QuanziMQService
 * @Author ppjjss
 * @Date: 2022/6/30 23:06
 * @Version 1.0
 */
@Service
public class QuanziMQService {

      private static final Logger LOGGER = LoggerFactory.getLogger(QuanziMQService.class);

      @Autowired
      private RocketMQTemplate rocketMQTemplate;

      @Reference
      private QuanZiApi quanZiApi;

      /**
       * 发布动态消息
       * @param publishId
       * @return
       */
      public Boolean publishMsg(String publishId){
            return this.sendMsg(publishId,1);
      }
      /**
       * 浏览动态消息
       *
       * @param publishId
       * @return
       */
      public Boolean queryPublishMsg(String publishId) {
            return this.sendMsg(publishId, 2);
      }

      public Boolean likePublishMsg(String publishId) {
            return this.sendMsg(publishId, 3);
      }

      /**
       * 取消点赞动态消息
       *
       * @param publishId
       * @return
       */
      public Boolean disLikePublishMsg(String publishId) {
            return this.sendMsg(publishId, 6);
      }

      /**
       * 喜欢动态消息
       *
       * @param publishId
       * @return
       */
      public Boolean lovePublishMsg(String publishId) {
            return this.sendMsg(publishId, 4);
      }

      /**
       * 取消喜欢动态消息
       *
       * @param publishId
       * @return
       */
      public Boolean disLovePublishMsg(String publishId) {
            return this.sendMsg(publishId, 7);
      }

      /**
       * 评论动态消息
       *
       * @param publishId
       * @return
       */
      public Boolean commentPublishMsg(String publishId) {
            return this.sendMsg(publishId, 5);
      }

      /**
       * 发送圈子操作相关的消息
       * @param publishId
       * @param type
       * @return
       */
      private Boolean sendMsg(String publishId, int type) {

            User user = UserThreadLocal.get();

            try {
                  Publish publish = this.quanZiApi.queryPublishById(publishId);
                  HashMap<String, Object> msg = new HashMap<>();
                  msg.put("userId",user.getId());
                  msg.put("date",System.currentTimeMillis());
                  msg.put("publishId",publishId);
                  msg.put("pid",publish.getPid());
                  msg.put("type",type);
                  this.rocketMQTemplate.convertAndSend("tanhua_quanzi",msg);
            } catch (MessagingException e) {
                  LOGGER.error("发送消息失败！publishId="+publishId+",type="+type,e);
                  return false;
            }

            return true;
      }



}
