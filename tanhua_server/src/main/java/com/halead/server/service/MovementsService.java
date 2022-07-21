package com.halead.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.halead.dubbo.api.QuanZiApi;
import com.halead.dubbo.pojo.Publish;
import com.halead.dubbo.vo.PageInfo;
import com.halead.server.pojo.User;
import com.halead.server.pojo.UserInfo;
import com.halead.server.utils.UserThreadLocal;
import com.halead.server.vo.Movements;
import com.halead.server.vo.PageResult;
import com.halead.server.vo.PicUploadResult;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.helpers.RelativeTimeDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @title: MovementsService
 * @Author ppjjss
 * @Date: 2022/6/30 23:04
 * @Version 1.0
 */
@Service
public class MovementsService {

    @Autowired
    private PicUploadService picUploadService;

    @Reference
    private QuanZiApi quanZiApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserInfoService userInfoService;


    /**
     * 发布动态
     *
     * @param textContent
     * @param location
     * @param longitude
     * @param latitude
     * @param multipartFile
     * @return
     */
    public String saveMovements(String textContent, String location, String longitude, String latitude, MultipartFile[] multipartFile) {
        User user = UserThreadLocal.get();
        Publish publish = new Publish();
        publish.setUserId(user.getId());
        publish.setText(textContent);
        publish.setLocationName(location);
        publish.setLatitude(latitude);
        publish.setLongitude(longitude);

        //图片上传
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : multipartFile) {
            PicUploadResult uploadResult = this.picUploadService.upload(file);
            imageUrls.add(uploadResult.getName());
        }
        publish.setMedias(imageUrls);
        return this.quanZiApi.savePublish(publish);
    }

    private PageResult queryPublishList(User user, Integer page, Integer pageSize) {

        PageResult pageResult = new PageResult();
        Long userId = null;
        PageInfo<Publish> pageInfo = null;
        if (null == user) {
            //查询推荐列表
            String key = "QUANZI_PUBLISH_RECOMMEND_" + UserThreadLocal.get().getId();
            String value = this.redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotEmpty(value)) {
                String[] pids = StringUtils.split(value, '.');
                //////////////////////////////////////////////////////////////////////////
                //TODO
            }
        }
        return null;
    }

    public PageResult queryRecommendPublishList(Integer page, Integer pageSize) {

        return this.queryPublishList(null, page, pageSize);
    }

    public Long likeComment(String publishId) {
        User user = UserThreadLocal.get();

        boolean bool = this.quanZiApi.saveLikeComment(user.getId(), publishId);
        if (!bool) {
            //保存失败
            return null;
        }
        //保存成功，获取点赞数
        Long likeCount = 0L;
        String likeCommentKey = "QUANZI_COMMMENT_LIKE_" + publishId;
        if (!this.redisTemplate.hasKey(likeCommentKey)) {
            Long count = this.quanZiApi.queryCommmentCount(publishId, 1);
            likeCount = count;
            this.redisTemplate.opsForValue().set(likeCommentKey, String.valueOf(likeCount));
        } else {
            likeCount = this.redisTemplate.opsForValue().increment(likeCommentKey);
        }
        //记录当前用于已经点赞
        String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + publishId;
        this.redisTemplate.opsForValue().set(likeCommentKey, "1");

        return likeCount;
    }

    public Long disLikeComment(String publishId) {
        User user = UserThreadLocal.get();
        boolean bool = this.quanZiApi.removeComment(user.getId(), publishId, 1);
        if (!bool) {
            return null;
        }
        //redis中的点赞数需要减一
        String likeCommentKey = "QUANZI_COMMENT_LIKE_" + publishId;
        Long count = this.redisTemplate.opsForValue().decrement(likeCommentKey);

        //删除该用户的标记点赞
        String likeUserComment = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + publishId;
        this.redisTemplate.delete(likeCommentKey);
        return count;
    }

    public Long loveComment(String publishId) {
        User user = UserThreadLocal.get();

        boolean bool = this.quanZiApi.saveLoveComment(user.getId(), publishId);
        if (!bool) {
            //保存失败
            return null;
        }
        //保存成功获取喜欢数
        Long loveCount = 0L;
        String likeCommmentKey = "QUANZI_COMMMENT_LOVE_" + publishId;
        if (!this.redisTemplate.hasKey(likeCommmentKey)) {
            Long count = this.quanZiApi.queryCommmentCount(publishId, 1);
            loveCount = count;
            this.redisTemplate.opsForValue().set(likeCommmentKey, String.valueOf(loveCount));
        }
        //记录当前用户已经喜欢
        String likeUserCommentKey = "QUANZI_COMMENT_LOVE_USER_" + user.getId() + "_" + publishId;
        this.redisTemplate.opsForValue().set(likeUserCommentKey, "1");
        return loveCount;
    }

    public Movements queryMovementsById(String publishId) {
        Publish publish = this.quanZiApi.queryPublishById(publishId);
        if (null == publish) {
            return null;
        }
        //查询到动态数据，数据填充
        List<Movements> movementsList = this.fillValueToMovements(Arrays.asList(publish));


        return null;
    }

    private List<Movements> fillValueToMovements(List<Publish> records) {

        User user = UserThreadLocal.get();
        List<Movements> movementList = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (Publish record : records) {
            Movements movements = new Movements();
            movements.setId(record.getId().toHexString());
            movements.setUserId(record.getUserId());
            if(!userIds.contains(record.getUserId())){
                userIds.add(record.getUserId());
            }
            String likeUserCommentKey ="QUANZI_COMMENT_LIKE_USER_"+user.getId()+"_"+movements.getId();
            movements.setHasLiked(this.redisTemplate.hasKey(likeUserCommentKey) ? 1:0);
            String likeCommentKey ="QUANZI_COMMENT_LIKE_"+movements.getId();
            String value = this.redisTemplate.opsForValue().get(likeCommentKey);
            if(StringUtils.isNotEmpty(value)){
                movements.setLikeCount(Integer.valueOf(value));
            }else {
                movements.setLikeCount(0);
            }
            String loveUserCommentKey ="QUANZI_COMMENT_LOVE_USER_"+user.getId()+"_"+movements.getId();
            movements.setHasLiked(this.redisTemplate.hasKey(loveUserCommentKey) ? 1:0);
            String loveCommentKey ="QUANZI_COMMENT_LOVE_"+movements.getId();
            String loveValue = this.redisTemplate.opsForValue().get(loveCommentKey);
            if(StringUtils.isNotEmpty(loveValue)){
                movements.setLoveCount(Integer.valueOf(loveValue));
            }else {
                movements.setLoveCount(0);
            }
            movements.setDistance("1公里"); //TODO 距离
            movements.setCommentCount(30); //TODO 评论数
            movements.setCreateDate("");
            movements.setTextContent(record.getText());
            movements.setImageContent(record.getMedias().toArray(new String[0]));

            movementList.add(movements);

        }


        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id",userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(wrapper);
        for (Movements movements : movementList) {
            for (UserInfo userInfo : userInfoList) {
                if(movements.getUserId().longValue() == userInfo.getUserId().longValue()){
                   movements.setTags(StringUtils.split(userInfo.getTags()));
                   movements.setNickname(userInfo.getNickName());
                   movements.setGender(userInfo.getSex().name().toLowerCase());
                   movements.setAvatar(userInfo.getLogo());
                   movements.setAge(userInfo.getAge());
                   break;
                }
            }
        }

        return movementList;
    }

    public String savePublish(String textContent, String location, String longitude, String latitude, MultipartFile multipartFile) {


        return null;
    }
}
