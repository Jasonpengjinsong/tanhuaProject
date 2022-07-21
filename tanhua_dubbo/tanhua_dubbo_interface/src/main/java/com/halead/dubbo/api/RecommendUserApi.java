package com.halead.dubbo.api;

import com.halead.dubbo.pojo.RecommendUser;
import com.halead.dubbo.vo.PageInfo;

public interface RecommendUserApi {

    /**
     * 查询以为得分最高的推荐用户
     * @param userId
     * @return
     */
    RecommendUser queryWithMaxScore(Long userId);

    /**
     * 按照得分倒序
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<RecommendUser> queryPageInfo(Long userId,Integer pageNum,Integer pageSize);

    /**
     * 推荐好友的缘分值
     * @param userId
     * @param toUserId
     * @return
     */
    double queryScore(Long userId,Long toUserId);
}
