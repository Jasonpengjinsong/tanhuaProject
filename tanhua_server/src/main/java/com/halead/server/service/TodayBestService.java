package com.halead.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.halead.dubbo.pojo.RecommendUser;
import com.halead.dubbo.vo.PageInfo;
import com.halead.server.pojo.User;
import com.halead.server.pojo.UserInfo;
import com.halead.server.utils.UserThreadLocal;
import com.halead.server.vo.PageResult;
import com.halead.server.vo.RecommendUserQueryParam;
import com.halead.server.vo.TodayBest;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @title: TodayBestService
 * @Author ppjjss
 * @Date: 2022/6/30 18:19
 * @Version 1.0
 */
@Service
public class TodayBestService {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RecommendUserService recommendUserService;
    @Value("${tanhua.sso.default.recommend.users}")
    private String defaultRecommendUser;


    public TodayBest queryTodayBest(Long userId) {

        User user = UserThreadLocal.get();
        TodayBest todayBest = new TodayBest();
        UserInfo userInfo = this.userInfoService.queryUserInfoById(userId);
        todayBest.setId(userId);
        todayBest.setAge(userInfo.getAge());
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setGender(userInfo.getSex().name().toLowerCase());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ","));

        double score = this.recommendUserService.queryScore(userId, user.getId());
        if (score == 0) {
            score = 98;
        }
        todayBest.setFateValue(Double.valueOf(score).longValue());
        return todayBest;
    }

    public PageResult queryRecommmendUserList(RecommendUserQueryParam param){
           //根据token获取当前的用户信息
        User user = UserThreadLocal.get();
        PageInfo<RecommendUser> pageInfo= this.recommendUserService.queryRecommendUserList(user.getId(),param.getPage(),param.getPagesize());
        List<RecommendUser> records = pageInfo.getRecords();
        //如果未查到，使用默认推荐列表
        if(CollectionUtils.isEmpty(records)){
            String[] ss = StringUtils.split(defaultRecommendUser, ',');
            for (String s : ss) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Long.valueOf(s));
                recommendUser.setToUserId(user.getId());
                recommendUser.setScore(RandomUtils.nextDouble(70,80));
                records.add(recommendUser);
            }
        }
       List<Long> userIds= new ArrayList<>();
        for (RecommendUser record : records) {
            userIds.add(record.getUserId());
        }

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("user_id",userIds);
        if(param.getAge() !=null){
            wrapper.lt("age",param.getAge());
        }
        if(StringUtils.isNotEmpty(param.getCity())){
            wrapper.eq("city",param.getCity());
        }
       List<UserInfo> userInfos= this.userInfoService.queryUserInfoList(wrapper);
        List<TodayBest> todayBests = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            TodayBest todayBest = new TodayBest();
            todayBest.setId(userInfo.getId());
            todayBest.setAge(userInfo.getAge());
            todayBest.setAvatar(userInfo.getLogo());
            todayBest.setGender(userInfo.getSex().name().toLowerCase());
            todayBest.setTags(StringUtils.split(userInfo.getTags(),','));

            for (RecommendUser record : records) {
                if(record.getUserId().longValue() == todayBest.getId().longValue()){
                    double score = Math.floor(record.getScore());
                    todayBest.setFateValue(Double.valueOf(score).longValue()); //缘分值
                }
            }
            todayBests.add(todayBest);
        }
        Collections.sort(todayBests,(o1,o2)->Long.valueOf(o2.getFateValue() - o1.getFateValue()).intValue());
        return new PageResult(0,param.getPagesize(),0,param.getPage(),todayBests);
    }
}
