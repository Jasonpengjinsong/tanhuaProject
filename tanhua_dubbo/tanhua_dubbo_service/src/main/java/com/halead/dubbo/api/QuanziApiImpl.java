package com.halead.dubbo.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.halead.dubbo.api.QuanZiApi;
import com.halead.dubbo.pojo.*;
import com.halead.dubbo.service.IdService;
import com.halead.dubbo.vo.PageInfo;
import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: QuanziApiImpl
 * @Author ppjjss
 * @Date: 2022/6/30 22:19
 * @Version 1.0
 */
@Service
public class QuanziApiImpl implements QuanZiApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdService idService;

    @Override
    public String savePublish(Publish publish) {


        //检验publish对象
        if(publish.getUserId() == null){
            return null;
        }

        try {
            //填充数据
            publish.setId(ObjectId.get());
            publish.setCreated(System.currentTimeMillis());
            publish.setSeeType(1); //查看权限

            //增加自增长的pid
            publish.setPid(this.idService.createId("publish",publish.getId().toHexString()));
            //保存动态信息
            this.mongoTemplate.save(publish);

            Album album = new Album();
            album.setId(ObjectId.get());
            album.setPublishId(publish.getId());
            album.setCreated(System.currentTimeMillis());

            //将相册对象写入到MongoDB中
            this.mongoTemplate.save(album,"quanzi_album_"+publish.getUserId());

            Query query = Query.query(Criteria.where("userId").is(publish.getUserId()));
            List<Users> users = this.mongoTemplate.find(query, Users.class);
            for (Users user : users) {
                TimeLine timeLine = new TimeLine();
                timeLine.setId(ObjectId.get());
                timeLine.setUserId(publish.getUserId());
                timeLine.setPublishId(publish.getId());
                timeLine.setDate(System.currentTimeMillis());

                this.mongoTemplate.save(timeLine,"quanzi_time_line_"+user.getFriendId());
            }

            return publish.getId().toHexString();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 事务回滚
        }
        return null;
    }

    @Override
    public PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize) {
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("date")));
        Query query = new Query().with(pageable);
        String tableName="quanzi_time_line_";
        if(null == userId){
            tableName+="recommend";
        }else {
            tableName+=userId;
        }
        //查询时间线表
        List<TimeLine> timeLines = this.mongoTemplate.find(query, TimeLine.class, tableName);
        List<ObjectId> ids  = new ArrayList<>();
        for (TimeLine timeLine : timeLines) {
            ids.add(timeLine.getPublishId());
        }
        Query queryPublish = Query.query(Criteria.where("id").in("ids")).with(Sort.by(Sort.Order.desc("created")));
        //查询动态信息表
        List<Publish> publishList = this.mongoTemplate.find(queryPublish, Publish.class);

        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(0); //不提供总数
        pageInfo.setRecords(publishList);
        return pageInfo;
    }

    @Override
    public boolean saveLikeComment(Long userId, String publishId) {
        //判断是否已经点赞，如果已经点赞就返回
        Criteria criteria = Criteria.where("userId").is(userId).and("publishId").is(new ObjectId(publishId)).and("commentType").is(1);
        Query query = new Query(criteria);
        long count = this.mongoTemplate.count(query, Comment.class);
        if(count >0){
            return false;
        }

        return this.saveComment(userId,publishId,1,null);
    }

    private boolean saveComment(Long userId, String publishId, int type, String content) {

        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setIsParent(true);
            comment.setCommentType(type);
            comment.setPublishId(new ObjectId(publishId));
            comment.setUserId(userId);
            comment.setId(ObjectId.get());
            comment.setCreated(System.currentTimeMillis());

            Publish publish = this.mongoTemplate.findById(comment.getPublishId(), Publish.class);
            if(null == publish){
                comment.setPublishUserId(publish.getUserId());
            }else {
                Video video = this.mongoTemplate.findById(comment.getPublishId(), Video.class);
                if(null !=video){
                    comment.setPublishUserId(video.getUserId());
                }
            }
            this.mongoTemplate.save(comment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeComment(Long userId, String publishId, Integer commentType) {
        Criteria criteria = Criteria.where("userId").is(userId).and("publishId").is(new ObjectId(publishId)).and("commentType").is(commentType);
        Query query = Query.query(criteria);
        DeleteResult deleteResult = this.mongoTemplate.remove(query, Comment.class);
        return deleteResult.getDeletedCount() >0;
    }

    @Override
    public boolean saveLoveComment(Long userId, String publishId) {
        //判断是否已经喜欢，如果喜欢就返回
        Criteria criteria = Criteria.where("userId").is(userId).and("publishId").is(new ObjectId(publishId)).and("commentType").is(3);
        Query query = Query.query(criteria);
        long count = this.mongoTemplate.count(query, Comment.class);
        if(count >0){
            return false;
        }
        return this.saveComment(userId,publishId,3,null);

    }

    @Override
    public Long queryCommmentCount(String publishId, Integer type) {

        Criteria criteria = Criteria.where("publishId").is(new ObjectId(publishId))
                .and("commentType").is(type);
        Query query = Query.query(criteria);
        return this.mongoTemplate.count(query, Comment.class);
    }

    @Override
    public Publish queryPublishById(String publishId) {

        return this.mongoTemplate.findById(new ObjectId(publishId),Publish.class);
    }

    @Override
    public PageInfo<Comment> queryCommentList(String publishId, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public List<Publish> queryPublishByPids(List<Long> pids) {
        return null;
    }

    @Override
    public PageInfo<Comment> queryCommentListByUser(Long userId, Integer type, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = new Query(Criteria.where("publishUserId").is(userId).and("commentType").is(type)).with(pageRequest);
        List<Comment> commentList = this.mongoTemplate.find(query, Comment.class);
        PageInfo<Comment> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(commentList);
        pageInfo.setTotal(0); //不提供总数
        return pageInfo;
    }

    @Override
    public PageInfo<Publish> queryAlbumList(Long userId, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public Long queryCommentCount(String publishId, int type) {
        Criteria criteria = Criteria.where("publishId").in(new ObjectId(publishId)).and("commentType").is(type);
        Query query = Query.query(criteria);
        return this.mongoTemplate.count(query,Comment.class);

    }

    @Override
    public Boolean saveCommet(Long userId, String publishId, int type, String content) {
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setIsParent(true);
            comment.setCommentType(type);
            comment.setPublishId(new ObjectId(publishId));
            comment.setUserId(userId);
            comment.setId(ObjectId.get());
            comment.setCreated(System.currentTimeMillis());
            //设置发布人的id
            Publish publish= this.mongoTemplate.findById(comment.getPublishId(),Publish.class);
            if(null != publish){
                comment.setPublishUserId(publish.getUserId());
            }else {
                Video video = this.mongoTemplate.findById(comment.getPublishId(), Video.class);
                if(null != video){
                    comment.setPublishUserId(video.getUserId());
                }
            }
            this.mongoTemplate.save(comment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
