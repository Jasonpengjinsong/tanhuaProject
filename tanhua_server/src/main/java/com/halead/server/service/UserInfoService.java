package com.halead.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.halead.server.mapper.UserInfoMapper;
import com.halead.server.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @title: UserInfoService
 * @Author ppjjss
 * @Date: 2022/6/30 18:30
 * @Version 1.0
 */
@Service
public class UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfo queryUserInfoById(Long userId) {
        return this.userInfoMapper.selectById(userId);
    }

    /**
     * 查询用户信息列表
     * @param wrapper
     * @return
     */
    public List<UserInfo> queryUserInfoList(QueryWrapper<UserInfo> wrapper) {
        return this.userInfoMapper.selectList(wrapper);
    }
}
