package com.halead.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.halead.dubbo.api.QuanZiApi;
import com.halead.dubbo.api.UserApi;
import com.halead.dubbo.pojo.Comment;
import com.halead.dubbo.pojo.Users;
import com.halead.dubbo.vo.PageInfo;
import com.halead.server.pojo.Announcement;
import com.halead.server.pojo.User;
import com.halead.server.pojo.UserInfo;
import com.halead.server.utils.UserThreadLocal;
import com.halead.server.vo.Contacts;
import com.halead.server.vo.MessageAnnouncement;
import com.halead.server.vo.MessageLike;
import com.halead.server.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @title: IMService
 * @Author ppjjss
 * @Date: 2022/7/3 20:40
 * @Version 1.0
 */
@Service
public class IMService {
    @Reference
    private UserApi userApi;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${tanhua.sso.url}")
    private String ssoUrl;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private QuanZiApi quanZiApi;
    @Autowired
    private AnnouncementService announcementService;


    public Boolean contactUser(Long userId) {
        Users users = new Users();
        users.setUserId(UserThreadLocal.get().getId());
        users.setFriendId(userId);

        this.userApi.saveUsers(users);
        String url = ssoUrl + "user/huanxin/contacts/" + users.getUserId() + "/" + users.getFriendId();
        ResponseEntity<Void> responseEntity = this.restTemplate.postForEntity(url, null, Void.class);
        if (responseEntity.getStatusCodeValue() == 200) {
            return true;
        }
        return false;

    }

    public PageResult queryContactsList(Integer page, Integer pageSize, String keyword) {
        User user = UserThreadLocal.get();
        List<Users> usersList = null;
        if (StringUtils.isNotEmpty(keyword)) {
            usersList = this.userApi.queryAllUsersList(user.getId());
        } else {
            PageInfo<Users> pageInfo = this.userApi.queryUsersList(user.getId(), page, pageSize);
            usersList = pageInfo.getRecords();
        }
        List<Long> fUserIds = new ArrayList<>();
        for (Users users : usersList) {
            fUserIds.add(users.getFriendId());
        }
        //查询用户（好友信息）
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id",fUserIds);
         if(StringUtils.isNotEmpty(keyword)){
             queryWrapper.like("nike_name",keyword);
         }
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        ArrayList<Contacts> contactsList = new ArrayList<>();
        if(StringUtils.isNotEmpty(keyword)){
             for (Users users : usersList) {
                 for (UserInfo userInfo : userInfoList) {
                     if(users.getFriendId().longValue() == userInfo.getId().longValue()){
                         Contacts contacts = new Contacts();
                         contacts.setCity(StringUtils.substringBefore(userInfo.getCity(),"-"));
                         contacts.setUserId(userInfo.getUserId().toString());
                         contacts.setNickname(userInfo.getNickName());
                         contacts.setGender(userInfo.getSex().name().toLowerCase());
                         contacts.setAvatar(userInfo.getLogo());
                         contacts.setAge(userInfo.getAge());

                         contactsList.add(contacts);

                         break;
                     }
                 }
             }
         }else {
            for (UserInfo userInfo : userInfoList) {
                Contacts contacts = new Contacts();
                contacts.setCity(StringUtils.substringBefore(userInfo.getCity(),"-"));
                contacts.setUserId(userInfo.getUserId().toString());
                contacts.setNickname(userInfo.getNickName());
                contacts.setGender(userInfo.getSex().name().toLowerCase());
                contacts.setAvatar(userInfo.getLogo());
                contacts.setAge(userInfo.getAge());

                contactsList.add(contacts);
            }
        }
        PageResult pageResult = new PageResult();
        pageResult.setPageSize(pageSize);
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        pageResult.setItems(contactsList);

        return pageResult;
    }

    public PageResult queryMessageLikeList(Integer page, Integer pageSize) {

        return this.messageCommentList(1,page,pageSize);
    }

    private PageResult messageCommentList(Integer type, Integer page, Integer pageSize) {
        User user = UserThreadLocal.get();
        PageInfo<Comment> pageInfo = this.quanZiApi.queryCommentListByUser(user.getId(), type, page, pageSize);
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        pageResult.setPageSize(pageSize);

        List<Comment> records = pageInfo.getRecords();
        if(CollectionUtils.isEmpty(records)){
            return pageResult;
        }
        List<Long> userIds = new ArrayList<>();
        for (Comment comment : records) {
            userIds.add(comment.getUserId());
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id",userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);

        ArrayList<MessageLike> messageLikeList = new ArrayList<>();
        for (Comment record : records) {
            for (UserInfo userInfo : userInfoList) {
                if (userInfo.getUserId().longValue() == record.getUserId().longValue()) {

                    MessageLike messageLike = new MessageLike();
                    messageLike.setId(record.getId().toHexString());
                    messageLike.setAvatar(userInfo.getLogo());
                    messageLike.setNickname(userInfo.getNickName());
                    messageLike.setCreateDate(new DateTime(record.getCreated()).toString("yyyy-MM-dd HH:mm"));

                    messageLikeList.add(messageLike);
                    break;
                }
            }
        }
        pageResult.setItems(messageLikeList);
        return pageResult;
    }

    public PageResult queryMessageCommentList(Integer page, Integer pageSize) {
        return this.messageCommentList(2,page,pageSize);

    }

    public PageResult queryMessageAnnouncementList(Integer page, Integer pageSize) {
        IPage<Announcement> announcementIPage = this.announcementService.queryList(page, pageSize);
        List<MessageAnnouncement> messageAnnouncementList = new ArrayList<>();
        for (Announcement record : announcementIPage.getRecords()) {
            MessageAnnouncement messageAnnouncement = new MessageAnnouncement();
            messageAnnouncement.setId(record.getId().toString());
            messageAnnouncement.setTitle(record.getTitle());
            messageAnnouncement.setDescription(record.getDescription());
            messageAnnouncement.setCreateDate(new DateTime(record.getCreated()).toString("yyyy-MM-dd HH:mm"));

            messageAnnouncementList.add(messageAnnouncement);
        }
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        pageResult.setPageSize(pageSize);
        pageResult.setItems(messageAnnouncementList);

        return pageResult;
    }
}
