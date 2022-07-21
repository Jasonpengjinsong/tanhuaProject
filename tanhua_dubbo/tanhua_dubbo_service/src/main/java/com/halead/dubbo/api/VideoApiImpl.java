package com.halead.dubbo.api;

import com.halead.dubbo.pojo.FollowUser;
import com.halead.dubbo.pojo.Video;
import com.halead.dubbo.service.IdService;
import com.halead.dubbo.vo.PageInfo;
import com.mongodb.client.result.DeleteResult;
import jdk.nashorn.internal.runtime.Version;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @title: VideoApiImpl
 * @Author ppjjss
 * @Date: 2022/7/3 12:07
 * @Version 1.0
 */

@Service()
public class VideoApiImpl implements VideoApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdService idService;

    @Override
    public String saveVideo(Video video) {
        if(video.getUserId() == null){
            return null;
        }
        video.setId(ObjectId.get());
        video.setCreated(System.currentTimeMillis());
        video.setVid(idService.createId("video",video.getId().toHexString())); //生成自增长的id
        this.mongoTemplate.save(video);
        return video.getId().toHexString();
    }

    @Override
    public PageInfo<Video> queryVideoList(Integer page, Integer pageSize) {
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = new Query().with(pageable);
        List<Video> videos = this.mongoTemplate.find(query, Video.class);
        PageInfo<Video> pageInfo = new PageInfo<>();
        pageInfo.setRecords(videos);
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(0); //先不提供总数
        return pageInfo;

    }

    @Override
    public Boolean followUser(Long userId, Long followUserId) {

        try {
            FollowUser followUser = new FollowUser();
            followUser.setId(ObjectId.get());
            followUser.setUserId(userId);
            followUser.setFollowUserId(followUserId);
            followUser.setCreated(System.currentTimeMillis());
            this.mongoTemplate.save(followUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean disFollowUser(Long userId, Long followUserId) {
        Criteria criteria = Criteria.where("userId").is(userId).and("followUserId").is(followUserId);
        Query query = Query.query(criteria);
        DeleteResult deleteResult = this.mongoTemplate.remove(query, FollowUser.class);

        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public Video queryVideoById(String id) {
        return null;
    }

    @Override
    public List<Video> queryVideoListByPids(List<Long> vids) {
        return null;
    }
}
