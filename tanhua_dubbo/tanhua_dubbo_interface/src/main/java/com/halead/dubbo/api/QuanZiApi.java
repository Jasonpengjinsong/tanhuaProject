package com.halead.dubbo.api;

import com.halead.dubbo.pojo.Comment;
import com.halead.dubbo.pojo.Publish;
import com.halead.dubbo.vo.PageInfo;

import java.util.List;

public interface QuanZiApi {

    /**
     * 发布动态
     * @param publish
     * @return
     */
    String savePublish(Publish publish);

    /**
     * 查询好友动态
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PageInfo<Publish> queryPublishList(Long userId,Integer page,Integer pageSize);

    /**
     * 点赞
     * @param userId
     * @param publishId
     * @return
     */
    boolean saveLikeComment(Long userId,String publishId);

    /**
     * 取消点赞，喜欢等
     * @param userId
     * @param publishId
     * @param commentType
     * @return
     */
    boolean removeComment(Long userId,String publishId,Integer commentType);

    /**
     * 喜欢
     * @param userId
     * @param publishId
     * @return
     */
    boolean saveLoveComment(Long userId,String publishId);

    /**
     * 查询评论数
     * @param publishID
     * @param type
     * @return
     */
    Long queryCommmentCount(String publishID,Integer type);

    /**
     * 根据id进行查询
     * @param publishId
     * @return
     */
    Publish queryPublishById(String publishId);

    /**
     * 查询评论
     * @param publishId
     * @param page
     * @param pageSize
     * @return
     */
    PageInfo<Comment> queryCommentList(String publishId,Integer page,Integer pageSize);

    /**
     * 根据pid批量查询数据
     * @param pids
     * @return
     */
    List<Publish> queryPublishByPids(List<Long> pids);

    /**
     * 查询用户的评论数据
     * @param userId
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    PageInfo<Comment> queryCommentListByUser(Long userId,Integer type,Integer page,Integer pageSize);

    /**
     * 查询相册表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PageInfo<Publish> queryAlbumList(Long userId,Integer page,Integer pageSize);

    /**
     * 查询评论数
     * @param id
     * @param i
     * @return
     */
    Long queryCommentCount(String id, int i);

    /**
     * 保存评论
     * @param id
     * @param publishId
     * @param i
     * @param content
     * @return
     */
    Boolean saveCommet(Long id, String publishId, int i, String content);




}
