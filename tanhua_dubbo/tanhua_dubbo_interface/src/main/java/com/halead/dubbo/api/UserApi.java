package com.halead.dubbo.api;

import com.halead.dubbo.pojo.Users;
import com.halead.dubbo.vo.PageInfo;

import java.util.List;

public interface UserApi {
    /**
     * 保存好友
     * @param users
     * @return
     */
    String saveUsers(Users users);

    /**
     * 根据用户id查询Users列表
     * @param userId
     * @return
     */
    List<Users> queryAllUsersList(Long userId);

    /**
     * 根据用户id查询Users列表(分页列表)
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PageInfo<Users> queryUsersList(Long userId,Integer page,Integer pageSize);
}
