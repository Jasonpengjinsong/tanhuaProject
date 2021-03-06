package com.halead.dubbo.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.halead.dubbo.pojo.Users;
import com.halead.dubbo.vo.PageInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


/**
 * @title: UserApiImpl
 * @Author ppjjss
 * @Date: 2022/7/3 20:03
 * @Version 1.0
 */
@Service(version = "1.0.0")
public class UserApiImpl implements UserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String saveUsers(Users users) {


        //校验
        if (users.getUserId() == null || users.getFriendId() == null) {
            return null;
        }

        Query query = Query.query(Criteria.where("userId").is(users.getUserId()).and("friendId").is(users.getFriendId()));
        Users oldUsers = this.mongoTemplate.findOne(query, Users.class);
        if (null != oldUsers) {
            //该好友的关系已经存在
            return null;
        }
        users.setId(ObjectId.get());
        users.setDate(System.currentTimeMillis());
        //将数据写入到mongodb中
        this.mongoTemplate.save(users);
        return users.getId().toHexString();
    }

    @Override
    public List<Users> queryAllUsersList(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return this.mongoTemplate.find(query,Users.class);
    }

    @Override
    public PageInfo<Users> queryUsersList(Long userId, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageRequest);
        List<Users> usersList = this.mongoTemplate.find(query, Users.class);
        PageInfo<Users> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(usersList);
        pageInfo.setTotal(0); //先不提供总数
        return pageInfo;
    }
}
