package com.halead.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.halead.dubbo.api.QuanZiApi;
import com.halead.dubbo.pojo.Comment;
import com.halead.dubbo.vo.PageInfo;
import com.halead.server.pojo.User;
import com.halead.server.pojo.UserInfo;
import com.halead.server.utils.UserThreadLocal;
import com.halead.server.vo.Comments;
import com.halead.server.vo.PageResult;
import jdk.nashorn.internal.ir.annotations.Reference;
import jdk.nashorn.internal.runtime.Version;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @title: CommentsService
 * @Author ppjjss
 * @Date: 2022/7/3 10:05
 * @Version 1.0
 */
@Service
public class CommentsService {

    @Reference()
    private QuanZiApi quanZiApi;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public PageResult queryCommentList(String publishId, Integer page, Integer pageSize) {
        User user = UserThreadLocal.get();
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setCounts(0);
        pageResult.setPages(0);
        PageInfo<Comment> pageInfo = this.quanZiApi.queryCommentList(publishId, page, pageSize);
        List<Comment> records = pageInfo.getRecords();
        if(CollectionUtils.isEmpty(records)){
            return pageResult;
        }
        List<Long> userIds = new ArrayList<>();
        for (Comment record : records){
            if(!userIds.contains(record.getUserId())){
                userIds.add(record.getUserId());
            }
        }

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id",userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(wrapper);

        List<Comments> commentsList = new ArrayList<>();
        for (Comment record : records) {

            Comments comments = new Comments();

            comments.setId(record.getId().toHexString());
            comments.setCreateDate(new DateTime(record.getCreated()).toString("yyyy年MM月dd日 HH:mm"));
            comments.setContent(record.getContent());
            for (UserInfo userInfo : userInfoList) {

                if(record.getUserId().longValue() == userInfo.getUserId().longValue()){
                    comments.setNickname(userInfo.getNickName());
                    comments.setAvatar(userInfo.getTags());
                    break;
                }
            }

            String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_"+user.getId()+"_"+comments.getId();

            comments.setHasLiked(this.redisTemplate.hasKey(likeUserCommentKey) ? 1:0);//是否点赞
            String likeCommentKey = "QUANZI_COMMENT_LIKE_"+comments.getId();
            String value = this.redisTemplate.opsForValue().get(likeCommentKey);
            if(!StringUtils.isNotEmpty(value)){
                comments.setLikeCount(Integer.valueOf(value));
            }else {
                comments.setLikeCount(0);
            }
            commentsList.add(comments);
        }
        pageResult.setItems(commentsList);

        return pageResult;
    }

    public Boolean saveComment(String publishId, String content) {
        User user = UserThreadLocal.get();
        return this.quanZiApi.saveCommet(user.getId(),publishId,2,content);
    }
}
