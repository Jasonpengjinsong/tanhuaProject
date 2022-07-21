package com.halead.recommend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/**
 * @title: RecommendVideo
 * @Author ppjjss
 * @Date: 2022/7/19 22:31
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendVideo {

    private ObjectId id;
    private Long userId;// 用户id
    private Long videoId; //视频id，需要转化为Long类型
    private Double score; //得分
    private Long date; //时间戳
}

