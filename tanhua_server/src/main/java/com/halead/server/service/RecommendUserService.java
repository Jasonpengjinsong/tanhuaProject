package com.halead.server.service;

import com.halead.dubbo.api.RecommendUserApi;
import com.halead.dubbo.pojo.RecommendUser;
import com.halead.dubbo.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @title: RecommendUserService
 * @Author ppjjss
 * @Date: 2022/6/30 18:37
 * @Version 1.0
 */
@Service
public class RecommendUserService {

    @Autowired
    private RecommendUserApi recommendUserApi;

    public double queryScore(Long userId, Long id) {

        return this.recommendUserApi.queryScore(userId, id);
    }

    public PageInfo<RecommendUser> queryRecommendUserList(Long id, Integer page, Integer pagesize) {

        return this.recommendUserApi.queryPageInfo(id,page,pagesize);
    }
}
