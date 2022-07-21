package com.halead.server.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.halead.server.mapper.AnnouncementMapper;
import com.halead.server.pojo.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @title: AnnouncementService
 * @Author ppjjss
 * @Date: 2022/7/3 23:13
 * @Version 1.0
 */
@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    public IPage<Announcement> queryList(Integer page,Integer pageSize){
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("created");
        return this.announcementMapper.selectPage(new Page<Announcement>(page,pageSize),wrapper);
    }
}
