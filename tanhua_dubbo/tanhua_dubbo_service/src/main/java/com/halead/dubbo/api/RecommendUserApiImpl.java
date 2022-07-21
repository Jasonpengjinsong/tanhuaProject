package com.halead.dubbo.api;

import com.halead.dubbo.api.RecommendUserApi;
import com.halead.dubbo.pojo.RecommendUser;
import com.halead.dubbo.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @title: RecommendUserApiImpl
 * @Author ppjjss
 * @Date: 2022/6/29 23:11
 * @Version 1.0
 */
@Service()
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser queryWithMaxScore(Long userId) {
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        return this.mongoTemplate.findOne(query,RecommendUser.class);
    }

    @Override
    public PageInfo<RecommendUser> queryPageInfo(Long userId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("score")));
        Query query = Query.query(Criteria.where("toUserId").is(userId)).with(pageable);
        List<RecommendUser> recommendUsers = this.mongoTemplate.find(query, RecommendUser.class);
        // 数据总数暂不提供，前端需要再实现
        return new PageInfo<>(0,pageNum,pageSize,recommendUsers);
    }

    @Override
    public double queryScore(Long userId, Long toUserId) {
        Query query = Query.query(Criteria.where("toUserId").is(toUserId).and("userId").is(userId));
        RecommendUser recommendUser = this.mongoTemplate.findOne(query, RecommendUser.class);
        if(null == recommendUser){
            return 0;
        }

        return recommendUser.getScore();
    }
}
