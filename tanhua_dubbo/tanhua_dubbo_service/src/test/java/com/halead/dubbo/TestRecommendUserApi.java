package com.halead.dubbo;

import com.halead.dubbo.api.RecommendUserApi;
import com.halead.dubbo.pojo.RecommendUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @title: TestRecommendUserApi
 * @Author ppjjss
 * @Date: 2022/6/29 23:30
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRecommendUserApi {
    @Autowired
    private RecommendUserApi recommendUserApi;

    @Test
    public void testQueryWithMaxScore(){
        RecommendUser recommendUser = this.recommendUserApi.queryWithMaxScore(1L);
        System.out.println(recommendUser);
    }

    @Test
    public void testList(){
        System.out.println(this.recommendUserApi.queryPageInfo(1L, 1, 5));
        System.out.println(this.recommendUserApi.queryPageInfo(1L, 2, 5));
        System.out.println(this.recommendUserApi.queryPageInfo(1L, 3, 5));
    }

}
